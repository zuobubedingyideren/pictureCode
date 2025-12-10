package com.px.pictureend.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend
 *
 * @author: idpeng
 * @version: 1.0
 * @className: DeleteRequest
 * @date: 2025/12/4 14:11
 * @description: 删除请求类
 */
@Data
public class DeleteRequest implements Serializable {

    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}
