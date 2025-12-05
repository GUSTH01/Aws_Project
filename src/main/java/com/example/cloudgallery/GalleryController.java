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

    @PostMapping("/image/delete/{id}")
    public String deleteImage(@PathVariable Long id) {
        try {
            // 1. Busca a imagem no banco
            Image image = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

            // 2. Deleta o arquivo do S3
            s3Service.deleteFile(image.getS3Url());

            // 3. Deleta o registro do banco de dados
            repository.deleteById(id);

            System.out.println("Imagem deletada com sucesso: ID " + id);

        } catch (Exception e) {
            System.err.println("Erro ao deletar imagem: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
