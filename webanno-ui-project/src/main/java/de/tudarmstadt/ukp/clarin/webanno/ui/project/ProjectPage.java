/*
 * Copyright 2021
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
 * and  Language Technology Universität Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.project;

import static de.tudarmstadt.ukp.clarin.webanno.api.SessionMetaData.CURRENT_PROJECT;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_PROJECT_ID;
import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportException;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportRequest;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportService;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportTaskMonitor;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectImportRequest;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.Tag;
import de.tudarmstadt.ukp.clarin.webanno.model.TagSet;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.ApplicationContextProvider;
import de.tudarmstadt.ukp.clarin.webanno.support.bootstrap.BootstrapAjaxTabbedPanel;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ChallengeResponseDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ConfirmationDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.ModelChangedVisitor;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.page.ApplicationPageBase;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelFactory;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelRegistry;
import de.tudarmstadt.ukp.clarin.webanno.ui.project.detail.ProjectDetailPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.project.guidelines.ProjectGuidelinesPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.project.users.ProjectUsersPanel;

/**
 * This is the main page for Project Settings. The Page has Four Panels. The
 * {@link ProjectGuidelinesPanel} is used to update documents to a project. The
 * {@code ProjectDetailsPanel} used for updating Project details such as descriptions of a project
 * and name of the Project The {@code ProjectTagSetsPanel} is used to add {@link TagSet} and
 * {@link Tag} details to a Project as well as updating them The {@link ProjectUsersPanel} is used
 * to update {@link User} to a Project
 */
@MountPath("/projectsetting.html")
public class ProjectPage
    extends ApplicationPageBase
{
    private static final Logger LOG = LoggerFactory.getLogger(ProjectPage.class);

    public static final String NEW_PROJECT_ID = "NEW";
    // this is hacky.. But to register a new "real" format, we would net to get the webanno-api
    // module back to override..
    public static final String COPY_PROJECT_FORMAT = "COPY_PROJECT_FORMAT";

    private static final long serialVersionUID = -2102136855109258306L;

    // private static final Logger LOG = LoggerFactory.getLogger(ProjectPage.class);

    private @SpringBean ProjectSettingsPanelRegistry projectSettingsPanelRegistry;
    private @SpringBean UserDao userRepository;
    private @SpringBean ProjectService projectService;
    private @SpringBean ProjectExportService exportService;

    private WebMarkupContainer sidebar;
    private WebMarkupContainer tabContainer;
    private AjaxTabbedPanel<ITab> tabPanel;
    private ProjectSelectionPanel projects;

    private IModel<Project> selectedProject;
    private ChallengeResponseDialog deleteProjectDialog;
    private ConfirmationDialog copyProjectDialog;

    private boolean preSelectedModelMode = false;

    public ProjectPage()
    {
        super();

        commonInit();
    }

    public ProjectPage(final PageParameters aPageParameters)
    {
        super(aPageParameters);

        commonInit();

        preSelectedModelMode = true;

        sidebar.setVisible(false);

        // Fetch project parameter
        StringValue projectParameter = aPageParameters.get(PAGE_PARAM_PROJECT_ID);
        // Check if we are asked to create a new project
        if (projectParameter != null && NEW_PROJECT_ID.equals(projectParameter.toString())) {
            selectedProject.setObject(new Project());
        }
        // Check if we are asked to open an existing project
        else {
            Optional<Project> project = getProjectFromParameters(projectParameter);
            if (project.isPresent()) {
                User user = userRepository.getCurrentUser();

                // Check access to project
                if (!userRepository.isAdministrator(user)
                        && !projectService.isManager(project.get(), user)) {
                    error("You have no permission to access project [" + project.get().getId()
                            + "]");
                    setResponsePage(getApplication().getHomePage());
                }

                selectedProject.setObject(project.get());
            }
            else {
                error("Project [" + projectParameter + "] does not exist");
                setResponsePage(getApplication().getHomePage());
            }
        }
    }

    private void commonInit()
    {
        selectedProject = Model.of();

        sidebar = new WebMarkupContainer("sidebar");
        sidebar.setOutputMarkupId(true);
        add(sidebar);

        tabContainer = new WebMarkupContainer("tabContainer");
        tabContainer.setOutputMarkupPlaceholderTag(true);
        tabContainer.add(visibleWhen(() -> selectedProject.getObject() != null));
        add(tabContainer);

        tabContainer.add(new Label("projectName", PropertyModel.of(selectedProject, "name")));

        tabContainer.add(new LambdaAjaxLink("cancel", this::actionCancel));

        tabContainer.add(new LambdaAjaxLink("delete", this::actionDelete)
                .onConfigure((_this) -> _this.setEnabled(selectedProject.getObject() != null
                        && selectedProject.getObject().getId() != null)));

        tabContainer.add(new LambdaAjaxLink("copy", this::actionCopy)
                .onConfigure((_this) -> _this.setEnabled(selectedProject.getObject() != null
                        && selectedProject.getObject().getId() != null)));

        tabPanel = new BootstrapAjaxTabbedPanel<ITab>("tabPanel", makeTabs())
        {
            private static final long serialVersionUID = -7356420977522213071L;

            @Override
            protected void onConfigure()
            {
                super.onConfigure();

                setVisible(selectedProject.getObject() != null);
            }
        };
        tabPanel.setOutputMarkupPlaceholderTag(true);
        tabContainer.add(tabPanel);

        projects = new ProjectSelectionPanel("projects", selectedProject);
        projects.setCreateAction(target -> {
            selectedProject.setObject(new Project());
            // Make sure that default values are loaded
            tabPanel.visitChildren(new ModelChangedVisitor(selectedProject));
        });
        projects.setChangeAction(target -> {
            target.add(tabContainer);
            // Make sure that any invalid forms are cleared now that we load the new project.
            // If we do not do this, then e.g. input fields may just continue showing the values
            // they had when they were marked invalid.
            tabPanel.visitChildren(new ModelChangedVisitor(selectedProject));
        });
        sidebar.add(projects);

        IModel<String> projectNameModel = PropertyModel.of(selectedProject, "name");
        add(deleteProjectDialog = new ChallengeResponseDialog("deleteProjectDialog",
                new StringResourceModel("DeleteProjectDialog.title", this),
                new StringResourceModel("DeleteProjectDialog.text", this).setModel(selectedProject)
                        .setParameters(projectNameModel),
                projectNameModel));
        deleteProjectDialog.setConfirmAction((target) -> {
            try {
                projectService.removeProject(selectedProject.getObject());
                if (preSelectedModelMode) {
                    setResponsePage(getApplication().getHomePage());
                }
                else {
                    selectedProject.setObject(null);
                    target.add(getPage());
                }
            }
            catch (IOException e) {
                LOG.error("Unable to remove project :" + ExceptionUtils.getRootCauseMessage(e));
                error("Unable to remove project " + ":" + ExceptionUtils.getRootCauseMessage(e));
                target.addChildren(getPage(), IFeedback.class);
            }
        });

        add(copyProjectDialog = new ConfirmationDialog("copyProjectDialog",
                new StringResourceModel("CopyProjectDialog.title", this, null),
                new StringResourceModel("CopyProjectDialog.text", this, null)));
        copyProjectDialog.setOutputMarkupId(true);

        copyProjectDialog.setConfirmAction((target) -> {
            try {
                this.copyProject();
                target.add(getPage());
            }
            catch (IOException e) {
                LOG.error("Unable to copy project :" + ExceptionUtils.getRootCauseMessage(e));
                error("Unable to copy project " + ":" + ExceptionUtils.getRootCauseMessage(e));
                target.addChildren(getPage(), IFeedback.class);
            }
        });
    }

    private void copyProject() throws IOException, ProjectExportException
    {
        // first we export the project (w/o the documents)
        File projectExportZipFile = this.exportProjectIntoMemoryZipFile();
        // then we import the project again
        this.importProjectFromInMemoryZipFile(projectExportZipFile);
    }

    private File exportProjectIntoMemoryZipFile() throws IOException, ProjectExportException
    {
        ProjectExportRequest exRequest = new ProjectExportRequest(selectedProject.getObject(),
                                                                  COPY_PROJECT_FORMAT,
                                                                  false);
        ProjectExportTaskMonitor monitor = new ProjectExportTaskMonitor();
        try {
            return exportService.exportProject(exRequest, monitor);
        }
        catch (ProjectExportException | IOException e) {
            // TODO what to do
            e.printStackTrace();
            throw e;
        }
    }

    private void importProjectFromInMemoryZipFile(File projectExportZipFile)
        throws IOException, ProjectExportException
    {
        User currentUser = userRepository.getCurrentUser();
        boolean currentUserIsAdministrator = userRepository.isAdministrator(currentUser);
        boolean currentUserIsProjectCreator = userRepository.isProjectCreator(currentUser);

        boolean createMissingUsers;
        boolean importPermissions;

        // Importing of permissions is only allowed if the importing user is an administrator
        if (currentUserIsAdministrator) {
            createMissingUsers = true;
            importPermissions = true;
        }
        // ... otherwise we force-disable importing of permissions so that the only remaining
        // permission for non-admin users is that they become the managers of projects they import.
        else {
            createMissingUsers = false;
            importPermissions = false;
        }

        // If the current user is an administrator and importing of permissions is *DISABLED*, we
        // configure the current user as a project manager. But if importing of permissions is
        // *ENABLED*, we do not set the admin up as a project manager because we would assume that
        // the admin wants to restore a project (maybe one exported from another instance) and in
        // that case we want to maintain the permissions the project originally had without adding
        // the admin as a manager.
        Optional<User> manager = Optional.empty();
        if (currentUserIsAdministrator) {
            if (!importPermissions) {
                manager = Optional.of(currentUser);
            }
        }
        // If the current user is NOT an admin but a project creator then we assume that the user is
        // importing the project for own use, so we add the user as a project manager.
        else if (currentUserIsProjectCreator) {
            manager = Optional.of(currentUser);
        }

        Project importedProject = null;

        ProjectImportRequest request = new ProjectImportRequest(createMissingUsers,
                importPermissions, manager);
        try {
            importedProject = exportService.importProject(request,
                    new ZipFile(projectExportZipFile));
        }
        catch (ProjectExportException | IOException e) {
            e.printStackTrace();
            throw e;
        }
        if (importedProject != null) {
            selectedProject.setObject(importedProject);
            Session.get().setMetaData(CURRENT_PROJECT, importedProject);
        }
    }

    private List<ITab> makeTabs()
    {
        List<ITab> tabs = new ArrayList<>();

        tabs.add(new AbstractTab(Model.of("Details"))
        {
            private static final long serialVersionUID = 6703144434578403272L;

            @Override
            public Panel getPanel(String panelId)
            {
                return new ProjectDetailPanel(panelId, selectedProject);
            }

            @Override
            public boolean isVisible()
            {
                return selectedProject.getObject() != null;
            }
        });

        // Add the project settings panels from the registry
        for (ProjectSettingsPanelFactory psp : projectSettingsPanelRegistry.getPanels()) {
            String path = psp.getPath();
            AbstractTab tab = new AbstractTab(Model.of(psp.getLabel()))
            {
                private static final long serialVersionUID = -1503555976570640065L;

                private ProjectSettingsPanelRegistry getRegistry()
                {
                    // @SpringBean doesn't work here and we cannot keep a reference on the
                    // projectSettingsPanelRegistry either because it is not serializable,
                    // so we have no other chance here than fetching it statically
                    return ApplicationContextProvider.getApplicationContext()
                            .getBean(ProjectSettingsPanelRegistry.class);
                }

                @Override
                public Panel getPanel(String aPanelId)
                {
                    return getRegistry().getPanel(path).createSettingsPanel(aPanelId,
                            selectedProject);
                }

                @Override
                public boolean isVisible()
                {
                    return selectedProject.getObject() != null
                            && selectedProject.getObject().getId() != null
                            && getRegistry().getPanel(path).applies(selectedProject.getObject());
                }
            };
            tabs.add(tab);
        }
        return tabs;
    }

    private Optional<Project> getProjectFromParameters(StringValue projectParam)
    {
        if (projectParam == null || projectParam.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(projectService.getProject(projectParam.toLong()));
        }
        catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private void actionCancel(AjaxRequestTarget aTarget)
    {
        if (preSelectedModelMode) {
            setResponsePage(getApplication().getHomePage());
        }
        else {
            selectedProject.setObject(null);

            // Reload whole page because master panel also needs to be reloaded.
            aTarget.add(getPage());
        }
    }

    private void actionDelete(AjaxRequestTarget aTarget)
    {
        deleteProjectDialog.show(aTarget);
    }

    private void actionCopy(AjaxRequestTarget aTarget)
    {
        copyProjectDialog.show(aTarget);
    }
}
