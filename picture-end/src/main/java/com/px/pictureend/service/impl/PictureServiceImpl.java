package com.px.pictureend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.exception.ThrowUtils;
import com.px.pictureend.manager.FileManager;
import com.px.pictureend.mapper.PictureMapper;
import com.px.pictureend.model.dto.file.UploadPictureResult;
import com.px.pictureend.model.dto.picture.PictureQueryRequest;
import com.px.pictureend.model.dto.picture.PictureUploadRequest;
import com.px.pictureend.model.entity.Picture;
import com.px.pictureend.model.entity.User;
import com.px.pictureend.model.vo.picture.PictureVO;
import com.px.pictureend.model.vo.user.UserVO;
import com.px.pictureend.service.PictureService;
import com.px.pictureend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * packageName: com.px.pictureend.service.impl
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureServiceImpl
 * @date: 2025/12/24 00:09
 * @description: 图片服务实现类
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    @Resource
    private FileManager fileManager;

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
     * @param multipartFile 上传的图片文件
     * @param pictureUploadRequest 图片上传请求对象，包含图片ID等信息
     * @param loginUser 当前登录用户
     * @return PictureVO 上传后的图片视图对象
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 校验用户是否登录
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long pictureId = null;

        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }

        // 校验图片ID是否存在，如果存在则验证图片记录是否存在
        if (pictureId != null) {
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }

        // 构建上传路径前缀
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);

        // 构建图片实体对象
        Picture picture = Picture.builder()
                .url(uploadPictureResult.getUrl())
                .name(uploadPictureResult.getPicName())
                .picSize(uploadPictureResult.getPicSize())
                .picWidth(uploadPictureResult.getPicWidth())
                .picHeight(uploadPictureResult.getPicHeight())
                .picScale(uploadPictureResult.getPicScale())
                .picFormat(uploadPictureResult.getPicFormat())
                .userId(loginUser.getId())
                .build();

        // 如果是更新操作，设置图片ID和编辑时间
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "上传图片失败");
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
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            // and (name like "%xxx%" or introduction like "%xxx%")
            queryWrapper.and(
                    qw -> qw.like("name", searchText)
                            .or()
                            .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            /* and (tag like "%\"Java\"%" and like "%\"Python\"%") */
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }
}
