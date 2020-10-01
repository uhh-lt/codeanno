package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.settings;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service.CodebookAutomationService;

public class AutomationSettingsPanel
    extends Panel
{

    private static final long serialVersionUID = -685340838123105638L;

    public static final String MID = "automationSettingsPanel";

    private static final String AUTOMATION_AVAILABLE = "alert-success";
    private static final String AUTOMATION_UNAVAILABLE = "alert-danger";

    private boolean automationAvailable;

    private @SpringBean CodebookAutomationService codebookAutomationService;

    public AutomationSettingsPanel()
    {
        super(MID);
    }

    public AutomationSettingsPanel(IModel<Codebook> model)
    {
        super(MID, model);

        // automation available message
        checkAutomationAvailable();
        createAutomationAvailableAlert();

        this.setVisible(model.getObject() != null);
        this.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();
        this.checkAutomationAvailable();
        this.createAutomationAvailableAlert();
        this.setVisible(this.getModelObject() != null);
    }

    private void checkAutomationAvailable()
    {
        try {
            if (this.getModelObject() != null)
                this.automationAvailable = codebookAutomationService
                        .automationIsAvailable(this.getModelObject(), true);
            else
                this.automationAvailable = false;
        }
        catch (ApiException e) {
            this.automationAvailable = false;
            e.printStackTrace();
        }
    }

    private void createAutomationAvailableAlert()
    {
        Label label = new Label("automationAvailableAlert", new StringResourceModel(
                automationAvailable ? "automation.available" : "automation.unavailable"));
        label.add(new AttributeAppender("class",
                automationAvailable ? AUTOMATION_AVAILABLE : AUTOMATION_UNAVAILABLE, " "));
        this.addOrReplace(label);
    }

    public Codebook getModelObject()
    {
        return (Codebook) this.getDefaultModelObject();
    }
}
