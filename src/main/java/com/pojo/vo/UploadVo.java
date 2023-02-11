package com.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 上传模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadVo {
    private List<MultipartFile> uploads;
    private String text;
}
