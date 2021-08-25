/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uhh.lt.codeanno.webapp.remoteapi;

import static de.uhh.lt.codeanno.webapp.remoteapi.aero.AeroRemoteApiController.API_BASE;
import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.context.WebApplicationContext;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ImportExportService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.RepositoryProperties;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.feature.FeatureSupportRegistry;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.feature.FeatureSupportRegistryImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.layer.ChainLayerSupport;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.layer.LayerSupportRegistry;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.layer.LayerSupportRegistryImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.layer.RelationLayerSupport;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.layer.SpanLayerSupport;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.AnnotationSchemaServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.BackupProperties;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.CasStorageServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.DocumentServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.ImportExportServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.casstorage.OpenCasStorageSessionForRequestFilter;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.export.ProjectExportServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportService;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.project.ProjectServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDaoImpl;
import de.tudarmstadt.ukp.clarin.webanno.security.model.Role;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.ApplicationContextProvider;
import de.tudarmstadt.ukp.clarin.webanno.text.TextFormatSupport;
import de.uhh.lt.codeanno.api.export.CodebookExporter;
import de.uhh.lt.codeanno.api.export.CodebookImportExportService;
import de.uhh.lt.codeanno.api.service.CodebookFeatureSupportRegistry;
import de.uhh.lt.codeanno.api.service.CodebookFeatureSupportRegistryImpl;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.api.service.CodebookSchemaServiceImpl;
import de.uhh.lt.codeanno.webapp.remoteapi.aero.AeroRemoteApiController;
import de.uhh.lt.codeanno.webapp.remoteapi.aero.model.RProjectMode;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, properties = {
        "repository.path=target/AeroRemoteApiControllerTest/repository" })
@EnableWebSecurity
@EntityScan({
        "de.tudarmstadt.ukp.clarin.webanno.model",
        "de.tudarmstadt.ukp.clarin.webanno.security.model",
        "de.uhh.lt.codeanno.ui.model"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AeroRemoteApiControllerTest
{
    private @Autowired WebApplicationContext context;
    private @Autowired UserDao userRepository;

    private MockMvc mvc;

    // If this is not static, for some reason the value is re-set to false before a
    // test method is invoked. However, the DB is not reset - and it should not be.
    // So we need to make this static to ensure that we really only create the user
    // in the DB and clean the test repository once!
    private static boolean initialized = false;

    @Before
    public void setup()
    {
        // @formatter:off
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .addFilters(new OpenCasStorageSessionForRequestFilter())
                .build();
        // @formatter:on

        if (!initialized) {
            userRepository.create(new User("admin", Role.ROLE_ADMIN));
            initialized = true;

            FileSystemUtils.deleteRecursively(new File("target/RemoteApiController2Test"));
        }
    }

    @Test
    public void t001_testProjectCreate() throws Exception
    {
        // @formatter:off
        mvc.perform(get(API_BASE + "/projects")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.messages").isEmpty());

        mvc.perform(post(API_BASE + "/projects")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "project1"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body.id").value("1"))
            .andExpect(jsonPath("$.body.name").value("project1"))
            .andExpect(jsonPath("$.body.mode").value(RProjectMode.annotation.name()));

        mvc.perform(get(API_BASE + "/projects")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].id").value("1"))
            .andExpect(jsonPath("$.body[0].name").value("project1"));
        /*
         * FIXME It's not possible to test the creation of projects for different project types
         * since the ProjectService injected at test time, does not know any project types..
         * Reproduce this problem by running this test in debug mode, set a breakpoint anywhere in
         * AeroRemoteApiController::projectCreate and list the available project types by executing
         * "projectService.listProjectTypes()". This list will be empty, what causes an
         * IllegalArgumentException when setting the mode because the mode i.e. project type is
         * unknown Anyways it works by testing manually (e.g. thru SwaggerUI)
         */
        /*
         * createProjectWithModeAndTest(RProjectMode.annotation);
         * createProjectWithModeAndTest(RProjectMode.correction);
         * createProjectWithModeAndTest(RProjectMode.automation);
         */

        // @formatter:on
    }

    /*
     * private void createProjectWithModeAndTest(RProjectMode mode) throws Exception {
     * mvc.perform(post(API_BASE + "/projects").with(csrf().asHeader())
     * .with(user("admin").roles("ADMIN")).contentType(MediaType.MULTIPART_FORM_DATA) .param("name",
     * "project1") .param("mode", mode.name())) .andExpect(status().isCreated())
     * .andExpect(content().contentType("application/json;charset=UTF-8"))
     * .andExpect(jsonPath("$.body.id").value("1"))
     * .andExpect(jsonPath("$.body.name").value("project1"))
     * .andExpect(jsonPath("$.body.mode").value(mode.name()));
     *
     * mvc.perform(get(API_BASE + "/projects").with(csrf().asHeader())
     * .with(user("admin").roles("ADMIN"))).andExpect(status().isOk())
     * .andExpect(content().contentType("application/json;charset=UTF-8"))
     * .andExpect(jsonPath("$.body[0].id").value("1"))
     * .andExpect(jsonPath("$.body[0].name").value("project1"))
     * .andExpect(jsonPath("$.body[0].mode").value(mode.name())); }
     */
    @Test
    public void t002_testDocumentCreate() throws Exception
    {
        // @formatter:off
        mvc.perform(get(API_BASE + "/projects/1/documents")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.messages").isEmpty());
        
        mvc.perform(multipart(API_BASE + "/projects/1/documents")
                .file("content", "This is a test.".getBytes("UTF-8"))
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN"))
                .param("name", "test.txt")
                .param("format", "text"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body.id").value("1"))
            .andExpect(jsonPath("$.body.name").value("test.txt"));
     
        mvc.perform(get(API_BASE + "/projects/1/documents")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].id").value("1"))
            .andExpect(jsonPath("$.body[0].name").value("test.txt"))
            .andExpect(jsonPath("$.body[0].state").value("NEW"));
        // @formatter:on
    }

    @Test
    public void t003_testAnnotationCreate() throws Exception
    {
        // @formatter:off
        mvc.perform(get(API_BASE + "/projects/1/documents/1/annotations")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.messages").isEmpty());
        
        mvc.perform(multipart(API_BASE + "/projects/1/documents/1/annotations/admin")
                .file("content", "This is a test.".getBytes("UTF-8"))
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN"))
                .param("name", "test.txt")
                .param("format", "text")
                .param("state", "IN-PROGRESS"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body.user").value("admin"))
            .andExpect(jsonPath("$.body.state").value("IN-PROGRESS"))
            .andExpect(jsonPath("$.body.timestamp").doesNotExist());
     
        mvc.perform(get(API_BASE + "/projects/1/documents/1/annotations")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].user").value("admin"))
            .andExpect(jsonPath("$.body[0].state").value("IN-PROGRESS"))
            .andExpect(jsonPath("$.body[0].timestamp").doesNotExist());
        // @formatter:on
    }

    @Test
    public void t004_testCurationCreate() throws Exception
    {
        // @formatter:off
        mvc.perform(get(API_BASE + "/projects/1/documents")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].id").value("1"))
            .andExpect(jsonPath("$.body[0].name").value("test.txt"))
            .andExpect(jsonPath("$.body[0].state").value("NEW"));
        
        mvc.perform(multipart(API_BASE + "/projects/1/documents/1/curation")
                .file("content", "This is a test.".getBytes("UTF-8"))
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN"))
                .param("name", "test.txt")
                .param("format", "text")
                .param("state", "CURATION-COMPLETE"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body.user").value("CURATION_USER"))
            .andExpect(jsonPath("$.body.state").value("COMPLETE"))
            .andExpect(jsonPath("$.body.timestamp").exists());
     
        mvc.perform(get(API_BASE + "/projects/1/documents")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].id").value("1"))
            .andExpect(jsonPath("$.body[0].name").value("test.txt"))
            .andExpect(jsonPath("$.body[0].state").value("CURATION-COMPLETE"));
        // @formatter:on
    }

    @Test
    public void t005_testCurationDelete() throws Exception
    {
        // @formatter:off
        mvc.perform(delete(API_BASE + "/projects/1/documents/1/curation")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN"))
                .param("projectId", "1")
                .param("documentId", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"));
     
        mvc.perform(get(API_BASE + "/projects/1/documents")
                .with(csrf().asHeader())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.body[0].id").value("1"))
            .andExpect(jsonPath("$.body[0].name").value("test.txt"))
            .andExpect(jsonPath("$.body[0].state").value("ANNOTATION-IN-PROGRESS"));
        // @formatter:on
    }

    @Configuration
    public static class TestContext
    {
        private @Autowired ApplicationEventPublisher applicationEventPublisher;
        private @Autowired EntityManager entityManager;

        @Bean
        public AeroRemoteApiController remoteApiV2()
        {
            return new AeroRemoteApiController();
        }

        @Bean
        public ProjectService projectService()
        {
            return new ProjectServiceImpl(userRepository(), applicationEventPublisher,
                    repositoryProperties(), null);
        }

        @Bean
        public UserDao userRepository()
        {
            return new UserDaoImpl();
        }

        @Bean
        public DocumentService documentService()
        {
            return new DocumentServiceImpl(repositoryProperties(), casStorageService(),
                    importExportService(), projectService(), applicationEventPublisher,
                    entityManager);
        }

        @Bean
        public AnnotationSchemaService annotationService()
        {
            return new AnnotationSchemaServiceImpl(layerSupportRegistry(), featureSupportRegistry(),
                    entityManager);
        }

        @Bean
        public CodebookSchemaService codebookSchemaService()
        {
            return new CodebookSchemaServiceImpl();
        }

        @Bean
        public CodebookImportExportService codebookImportExportService()
        {
            return new CodebookExporter();
        }

        @Bean
        public CodebookFeatureSupportRegistry codebookFeatureSupportRegistry()
        {
            return new CodebookFeatureSupportRegistryImpl(Collections.emptyList());
        }
        @Bean
        public FeatureSupportRegistry featureSupportRegistry()
        {
            return new FeatureSupportRegistryImpl(Collections.emptyList());
        }

        @Bean
        public CasStorageService casStorageService()
        {
            return new CasStorageServiceImpl(null, null, repositoryProperties(),
                    backupProperties());
        }

        @Bean
        public ImportExportService importExportService()
        {
            return new ImportExportServiceImpl(repositoryProperties(),
                    asList(new TextFormatSupport()), casStorageService(), annotationService(),
                    codebookImportExportService(), codebookSchemaService());
        }

        @Bean
        public CurationDocumentService curationDocumentService()
        {
            return new CurationDocumentServiceImpl();
        }

        @Bean
        public ProjectExportService exportService()
        {
            return new ProjectExportServiceImpl(null, null, projectService());
        }

        @Bean
        public RepositoryProperties repositoryProperties()
        {
            return new RepositoryProperties();
        }

        @Bean
        public BackupProperties backupProperties()
        {
            return new BackupProperties();
        }

        @Bean
        public ApplicationContextProvider contextProvider()
        {
            return new ApplicationContextProvider();
        }

        @Bean
        public LayerSupportRegistry layerSupportRegistry()
        {
            return new LayerSupportRegistryImpl(
                    asList(new SpanLayerSupport(featureSupportRegistry(), null, null),
                            new RelationLayerSupport(featureSupportRegistry(), null, null),
                            new ChainLayerSupport(featureSupportRegistry(), null, null)));
        }
    }
}
