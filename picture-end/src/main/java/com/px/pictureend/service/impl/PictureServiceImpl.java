package com.px.pictureend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pictureend.exception.BusinessException;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.exception.ThrowUtils;
import com.px.pictureend.mapper.PictureMapper;
import com.px.pictureend.model.dto.file.UploadPictureResult;
import com.px.pictureend.model.dto.picture.PictureQueryRequest;
import com.px.pictureend.model.dto.picture.PictureReviewRequest;
import com.px.pictureend.model.dto.picture.PictureUploadByBatchRequest;
import com.px.pictureend.model.dto.picture.PictureUploadRequest;
import com.px.pictureend.model.entity.Picture;
import com.px.pictureend.model.entity.User;
import com.px.pictureend.model.enums.PictureReviewStatusEnum;
import com.px.pictureend.model.vo.picture.PictureVO;
import com.px.pictureend.model.vo.user.UserVO;
import com.px.pictureend.service.PictureService;
import com.px.pictureend.service.UserService;
import com.px.pictureend.upload.FilePictureUpload;
import com.px.pictureend.upload.PictureUploadTemplate;
import com.px.pictureend.upload.UrlPictureUpload;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.pictureend.constant.PictureConstants.*;

/**
 * packageName: com.px.pictureend.service.impl
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureServiceImpl
 * @date: 2025/12/24 00:09
 * @description: 图片服务实现类
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    @Resource
    private FilePictureUpload filePictureUpload;

	@Resource
	private UrlPictureUpload urlPictureUpload;

    @Resource
    private UserService userService;


    /**
     * 验证图片对象的参数有效性
     * 对图片对象的基本信息进行校验，包括图片ID、URL和简介等字段的非空和长度校验
     *
     * @param picture 待验证的图片对象，不能为空
     */
    @Override
    public void validPicture(Picture picture) {
        // 待验证的图片对象不能为空
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);

        // 获取图片对象中的各个字段，包括图片ID、URL和简介等
        Long pictureId = picture.getId();
        String pictureUrl = picture.getUrl();
        String introduction = picture.getIntroduction();

        // 校验图片ID是否为空
        ThrowUtils.throwIf(ObjectUtil.isNull(pictureId), ErrorCode.PARAMS_ERROR, "图片id不能为空");

        // 校验图片URL长度，如果URL不为空则检查长度不能超过1024个字符
        if (StrUtil.isNotBlank(pictureUrl)) {
            ThrowUtils.throwIf(pictureUrl.length() > 1024, ErrorCode.PARAMS_ERROR, "图片url不能超过1024个字符");
        }
        // 校验图片简介长度，如果简介不为空则检查长度不能超过1024个字符
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 1024, ErrorCode.PARAMS_ERROR, "图片简介不能超过1024个字符");
        }
    }


        /**
         * 上传图片
         *
         * @param inputSource          上传的图片文件
         * @param pictureUploadRequest 图片上传请求对象，包含图片ID等信息
         * @param loginUser            当前登录用户
         * @return PictureVO 上传后的图片视图对象
         */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 校验用户是否登录
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long pictureId = null;

        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }

        // 如果是更新图片（pictureId 不为空），需要校验图片是否存在及权限
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或管理员可编辑图片
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 构建上传路径前缀
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
	    PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
	    if (inputSource instanceof String) {
		    pictureUploadTemplate = urlPictureUpload;
	    }
	    UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
	    // 构造要入库的图片信息
	    Picture picture = new Picture();
	    picture.setUrl(uploadPictureResult.getUrl());
	    // 支持外层传递图片名称
	    String picName = uploadPictureResult.getPicName();
	    if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
		    picName = pictureUploadRequest.getPicName();
	    }
	    picture.setName(picName);
	    picture.setPicSize(uploadPictureResult.getPicSize());
	    picture.setPicWidth(uploadPictureResult.getPicWidth());
	    picture.setPicHeight(uploadPictureResult.getPicHeight());
	    picture.setPicScale(uploadPictureResult.getPicScale());
	    picture.setPicFormat(uploadPictureResult.getPicFormat());
	    picture.setUserId(loginUser.getId());
	    // 补充审核参数
	    this.fillReviewParams(picture, loginUser);
	    // 操作数据库
	    // 如果 pictureId 不为空，表示更新，否则是新增
	    if (pictureId != null) {
		    // 如果是更新，需要补充 id 和编辑时间
		    picture.setId(pictureId);
		    picture.setEditTime(new Date());
	    }

	    boolean result = this.saveOrUpdate(picture);
	    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败，数据库操作失败");
	    return PictureVO.objToVo(picture);
    }


    /**
     * 获取图片视图对象
     * 将图片实体对象转换为视图对象，并关联对应的用户信息
     *
     * @param picture 图片实体对象，不能为空
     * @param request HTTP请求对象，用于获取请求上下文信息
     * @return PictureVO 转换后的图片视图对象，包含图片基本信息和关联的用户信息
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objToVo(picture);

        // 根据图片的用户ID获取并设置用户视图对象
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }


    /**
     * 将图片分页数据转换为图片视图对象分页数据
     * 该方法将Picture实体对象分页转换为PictureVO视图对象分页，并填充关联的用户信息
     *
     * @param picturePage 包含Picture实体对象的分页数据
     * @param request HTTP请求对象，用于获取请求相关信息
     * @return 包含PictureVO视图对象的分页数据
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }

        // 将Picture实体列表转换为PictureVO视图对象列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());

        // 获取所有图片对应的用户ID集合，用于批量查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        // 批量查询用户信息并按用户ID分组
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 为每个PictureVO对象填充对应的用户信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;

    }



    /**
     * 根据查询请求对象构建Picture实体的查询包装器
     * 该方法从PictureQueryRequest对象中提取各个查询条件，并将其转换为QueryWrapper中的查询条件
     *
     * @param pictureQueryRequest 查询请求对象，包含各种查询条件和排序字段
     * @return QueryWrapper<Picture> 构建好的查询包装器，用于数据库查询
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        // 搜索文本条件：在名称和介绍字段中进行模糊匹配
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            // and (name like "%xxx%" or introduction like "%xxx%")
            queryWrapper.and(
                    qw -> qw.like("name", searchText)
                            .or()
                            .like("introduction", searchText)
            );
        }
        // 构建各个字段的查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);

        // 标签查询：对JSON数组格式的标签字段进行匹配
        if (CollUtil.isNotEmpty(tags)) {
            /* and (tag like "%\"Java\"%" and like "%\"Python\"%") */
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        // 排序设置：根据指定字段和排序方向进行排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

	/**
	 * 执行图片审核操作
	 * 该方法用于审核图片，更新图片的审核状态、审核人和审核时间等信息
	 *
	 * @param pictureReviewRequest 图片审核请求对象，包含待审核的图片ID、审核状态和审核意见等信息，不能为空
	 * @param loginUser 当前登录用户，作为审核人信息，不能为空
	 */
	@Override
	public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
		ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = pictureReviewRequest.getId();
		Integer reviewStatus = pictureReviewRequest.getReviewStatus();
		PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
		String reviewMessage = pictureReviewRequest.getReviewMessage();
		if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 2. 判断图片是否存在
		Picture oldPicture = this.getById(id);
		ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
		// 3. 校验审核状态是否重复，已是改状态
		if (oldPicture.getReviewStatus().equals(reviewStatus)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
		}
		// 4. 数据库操作
		Picture updatePicture = new Picture();
		BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
		updatePicture.setReviewerId(loginUser.getId());
		updatePicture.setReviewTime(new Date());
		boolean result = this.updateById(updatePicture);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核失败");
	}


	/**
	 * 填充图片审核参数
	 * 根据用户权限为图片对象设置相应的审核参数，管理员直接审核通过，普通用户上传的图片设为审核中状态
	 *
	 * @param picture 待填充审核参数的图片对象，不能为空
	 * @param loginUser 当前登录用户，用于判断用户权限和设置审核人信息
	 */
	@Override
	public void fillReviewParams(Picture picture, User loginUser) {
		if (userService.isAdmin(loginUser)) {
			picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
			picture.setReviewerId(loginUser.getId());
			picture.setReviewMessage("管理员审核通过");
			picture.setReviewTime(new Date());
		} else {
			picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
		}
	}

	/**
	 * 批量上传图片
	 * 通过搜索关键词从外部网站批量获取图片并上传到系统中，支持设置图片名称前缀和上传数量限制
	 *
	 * @param pictureUploadByBatchRequest 批量上传图片请求对象，包含搜索关键词、名称前缀和上传数量等信息
	 * @param loginUser 当前登录用户，用于权限验证和设置图片上传者信息
	 * @return 实际成功上传的图片数量，如果搜索不到图片或出现错误则返回0
	 */
	@Override
	public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
		String searchText = pictureUploadByBatchRequest.getSearchText();
		String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
		if (StrUtil.isBlank(namePrefix)) {
			namePrefix = searchText;
		}

		// 验证上传数量限制
		Integer count = pictureUploadByBatchRequest.getCount();
		ThrowUtils.throwIf(count > BATCH_UPLOAD_MAX_COUNT, ErrorCode.PARAMS_ERROR,
				String.format("最多只能上传%d张图片", BATCH_UPLOAD_MAX_COUNT));

		// 构建抓取URL
		String fetchUrl = String.format(DEFAULT_URL, searchText);
		Document document;

		try {
			// 设置连接超时和读取超时
			document = Jsoup.connect(fetchUrl)
					.timeout(NETWORK_TIMEOUT_MS)
					.userAgent(USER_AGENT)
					.get();
		} catch (IOException e) {
			log.error("批量上传失败：无法获取搜索页面，searchText: {}, url: {}", searchText, fetchUrl, e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "网络请求失败，请稍后重试");
		}

		// 解析页面元素
		Element div = document.getElementsByClass("dgControl").first();
		if (ObjUtil.isNull(div)) {
			log.warn("批量上传失败：页面结构发生变化，无法找到图片容器，searchText: {}", searchText);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "页面解析失败，请稍后重试");
		}

		Elements imgElementList = div.select("img.mimg");
		if (CollUtil.isEmpty(imgElementList)) {
			log.warn("批量上传失败：未找到任何图片，searchText: {}", searchText);
			return 0;
		}

		int uploadCount = 0;
		int failedCount = 0;

		for (Element imgElement : imgElementList) {
			if (uploadCount >= count) {
				break;
			}

			String fileUrl = imgElement.attr("src");
			if (StrUtil.isBlank(fileUrl)) {
				log.debug("跳过空链接图片");
				continue;
			}

			// 清理URL参数
			fileUrl = cleanImageUrl(fileUrl);

			// 构建上传请求
			PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
			if (StrUtil.isNotBlank(namePrefix)) {
				pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
			}

			try {
				PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
				log.info("批量上传成功：图片ID = {}, URL = {}", pictureVO.getId(), fileUrl);
				uploadCount++;
			} catch (Exception e) {
				failedCount++;
				log.warn("批量上传失败：单张图片上传失败，URL = {}, 错误信息: {}", fileUrl, e.getMessage());

				// 如果失败次数过多，提前结束
				if (failedCount > MAX_FAILED_ATTEMPTS) {
					log.warn("批量上传中止：连续失败次数过多，已上传 {} 张，失败 {} 张", uploadCount, failedCount);
					break;
				}
			}
		}

		log.info("批量上传完成：成功 {} 张，失败 {} 张，搜索关键词: {}", uploadCount, failedCount, searchText);
		return uploadCount;

	}

	/**
	 * 清理图片URL中的参数
	 * 移除URL中的查询参数部分，只保留基础图片地址
	 *
	 * @param fileUrl 原始图片URL，可能包含查询参数
	 * @return 清理后的图片URL，不包含查询参数
	 */
	private String cleanImageUrl(String fileUrl) {
		int questionMarkIndex = fileUrl.indexOf("?");
		return questionMarkIndex > -1 ? fileUrl.substring(0, questionMarkIndex) : fileUrl;
	}
}
