package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticleRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Api(description = "Articles")
@RequestMapping("/api/articles")
@RestController
@Slf4j
public class ArticlesController extends ApiController{
    @Autowired
    ArticleRepository articleRepository;

    @ApiOperation(value = "List all articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Article> allCommonss() {
        Iterable<Article> commons = articleRepository.findAll();
        return commons;
    }

    @ApiOperation(value = "Get a single article")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Article getById(
            @ApiParam("id") @RequestParam Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        return article;
    }

    @ApiOperation(value = "Create a new article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Article postArticle(
            @ApiParam("title") @RequestParam String title,
            @ApiParam("url") @RequestParam String url,
            @ApiParam("explanation") @RequestParam String explanation,
            @ApiParam("email") @RequestParam String email,
            @ApiParam("date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAdded)
            throws JsonProcessingException {

        Article art = Article.builder()
                .title(title)
                .url(url)
                .explanation(explanation)
                .email(email)
                .dateAdded(dateAdded)
                .build();
        Article article = articleRepository.save(art);

        return article;
    }

    @ApiOperation(value = "Delete a Article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteArticle(
            @ApiParam("id") @RequestParam Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        articleRepository.delete(article);
        return genericMessage("Article with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a Article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Article updateArticle(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Article article) {

        Article article1 = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        article1.setDateAdded(article.getDateAdded());
        article1.setExplanation(article.getExplanation());
        article1.setTitle(article.getTitle());
        article1.setUrl(article.getUrl());
        article1.setEmail(article.getEmail());

        articleRepository.save(article1);
        return article1;
    }
}
