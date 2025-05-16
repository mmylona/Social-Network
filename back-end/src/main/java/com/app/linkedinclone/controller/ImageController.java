package com.app.linkedinclone.controller;

import com.app.linkedinclone.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/image")
@CrossOrigin(origins = "https://localhost:3000")
public class ImageController {
    private final ImageService imageServiceImpl;
    @GetMapping("init/images")
    public void initAllUserImages() {
        imageServiceImpl.initAllUserImages();
    }
}
