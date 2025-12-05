package com.example.cloudgallery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
public class GalleryController {

    @Autowired
    private ImageRepository repository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private LambdaService lambdaService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("images", repository.findAll());
        return "list";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        try {
            // 1. Upload pro S3
            String imageUrl = s3Service.uploadFile(file);

            // 2. Salva no Banco (RDS)
            Image image = new Image(title, description, imageUrl);
            Image savedImage = repository.save(image);

            // 3. Chama a Lambda
            lambdaService.triggerAudit(savedImage.getId());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
