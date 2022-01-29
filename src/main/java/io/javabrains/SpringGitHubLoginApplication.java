package io.javabrains;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.javabrains.emails.EmailListItem;
import io.javabrains.emails.EmailListItemKey;
import io.javabrains.emails.EmailListItemRepository;
import io.javabrains.folder.DataStaxAstraProperties;
import io.javabrains.folder.Folder;
import io.javabrains.folder.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class SpringGitHubLoginApplication {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	EmailListItemRepository emailListItemRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringGitHubLoginApplication.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init() {
		folderRepository.save(new Folder("etomasw", "Inbox", "blue"));
		folderRepository.save(new Folder("etomasw", "Sent", "green"));
		folderRepository.save(new Folder("etomasw", "Important", "yellow"));

		for(int i=0; i<10; i++) {
			EmailListItemKey key = new EmailListItemKey();
			key.setId("etomasw");
			key.setLabel("Inbox");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("etomasw"));
			item.setSubject("Subject ID: " + i);
			item.setUnread(true);

			emailListItemRepository.save(item);
		}


	}

}
