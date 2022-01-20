package io.javabrains.controller;

import io.javabrains.folder.Folder;
import io.javabrains.folder.FolderRepository;
import io.javabrains.folder.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InboxController {

    private FolderRepository folderRepository;
    private FolderService folderService;

    @Autowired
    public InboxController(FolderRepository folderRepository, FolderService folderService) {
        this.folderRepository = folderRepository;
        this.folderService = folderService;
    }

    @GetMapping(value = "/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if(principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");
            // We have three folders per user, so we need to iterate.
            List<Folder> userFolders = folderRepository.findAllById(userId);
            model.addAttribute("userFolders", userFolders);

            List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
            model.addAttribute("defaultFolders", defaultFolders);
            return "inbox-page";
        }

    }
}
