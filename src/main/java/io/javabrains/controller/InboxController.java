package io.javabrains.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.javabrains.emails.EmailListItem;
import io.javabrains.emails.EmailListItemRepository;
import io.javabrains.folder.Folder;
import io.javabrains.folder.FolderRepository;
import io.javabrains.folder.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class InboxController {

    private FolderRepository folderRepository;
    private FolderService folderService;
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    public InboxController(FolderRepository folderRepository, FolderService folderService, EmailListItemRepository emailListItemRepository) {
        this.folderRepository = folderRepository;
        this.folderService = folderService;
        this.emailListItemRepository = emailListItemRepository;
    }

    @GetMapping(value = "/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if(principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        } else {

            // Fetch folders
            String userId = principal.getAttribute("login");
            // We have three folders per user, so we need to iterate.
            List<Folder> userFolders = folderRepository.findAllById(userId);
            model.addAttribute("userFolders", userFolders);

            List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
            model.addAttribute("defaultFolders", defaultFolders);

            // Fetch messages
            String folderLabel = "Inbox";
            List<EmailListItem> emaiList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folderLabel);
            PrettyTime prettyTime = new PrettyTime();
            emaiList.stream().forEach(emailListItem -> {
                UUID uuid = emailListItem.getKey().getTimeUUID();
                Date date = new Date(Uuids .unixTimestamp(uuid));
                emailListItem.setAgoTimeString(prettyTime.format(date));
            });
            model.addAttribute("emailList", emaiList);



            return "inbox-page";
        }

    }
}
