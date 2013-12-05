/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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
 ******************************************************************************/
package de.tudarmstadt.ukp.clarin.webanno.brat.annotation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import de.tudarmstadt.ukp.clarin.webanno.api.RepositoryService;
import de.tudarmstadt.ukp.clarin.webanno.brat.controller.AnnotationTypeConstant;
import de.tudarmstadt.ukp.clarin.webanno.brat.controller.ArcAdapter;
import de.tudarmstadt.ukp.clarin.webanno.brat.controller.ChainAdapter;
import de.tudarmstadt.ukp.clarin.webanno.brat.controller.SpanAdapter;
import de.tudarmstadt.ukp.clarin.webanno.brat.message.GetDocumentResponse;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;

/**
 * Displays a BRAT visualisation and fills it with data from an {@link AnnotationDocument}. We do
 * not use a CAS as model object because the CAS is large and does not serialize well. It is easier
 * to drive this component using a reference to the CAS (here an {@link AnnotationDocument}) and let
 * the component fetch the associated CAS itself when necessary.
 *
 * @author Richard Eckart de Castilho
 */
public class BratAnnotationDocumentVisualizer
    extends BratVisualizer
{
    private static final long serialVersionUID = -5898873898138122798L;

    private boolean dirty = true;

    private String docData = EMPTY_DOC;

    @SpringBean(name = "jsonConverter")
    private MappingJacksonHttpMessageConverter jsonConverter;

    @SpringBean(name = "documentRepository")
    private RepositoryService repository;

    public BratAnnotationDocumentVisualizer(String id, IModel<AnnotationDocument> aModel)
    {
        super(id, aModel);
    }

    public void setModel(IModel<AnnotationDocument> aModel)
    {
        setDefaultModel(aModel);
    }

    public void setModelObject(AnnotationDocument aModel)
    {
        setDefaultModelObject(aModel);
    }

    @SuppressWarnings("unchecked")
    public IModel<AnnotationDocument> getModel()
    {
        return (IModel<AnnotationDocument>) getDefaultModel();
    }

    public AnnotationDocument getModelObject()
    {
        return (AnnotationDocument) getDefaultModelObject();
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();

        dirty = true;
    }

    @Override
    protected String getDocumentData()
    {
        if (!dirty) {
            return docData;
        }

        dirty = false;

        // Clear the rendered document
        docData = EMPTY_DOC;

        // Check if a document is set
        if (getModelObject() == null) {
            return docData;
        }

        // Get CAS from the repository
        JCas jCas = null;
        try {
            jCas = repository.getAnnotationDocumentContent(getModelObject());
        }
        catch (UIMAException e) {
            error(ExceptionUtils.getRootCauseMessage(e));
            return docData;
        }
        catch (DataRetrievalFailureException e) {
            error(e.getCause().getMessage());
        }
        catch (IOException e) {
            error("Unable to find annotation document " + ExceptionUtils.getRootCauseMessage(e));
        }
        catch (ClassNotFoundException e) {
            error("Unable to get the class name for conversion +"
                    + ExceptionUtils.getRootCauseMessage(e));
        }
        // Generate BRAT object model from CAS
        GetDocumentResponse response = new GetDocumentResponse();
        response.setText(jCas.getDocumentText());

        List<String> tagSetNames = new ArrayList<String>();
        tagSetNames.add(AnnotationTypeConstant.POS);
        tagSetNames.add(AnnotationTypeConstant.DEPENDENCY);
        tagSetNames.add(AnnotationTypeConstant.NAMEDENTITY);
        tagSetNames.add(AnnotationTypeConstant.COREFERENCE);
        tagSetNames.add(AnnotationTypeConstant.COREFRELTYPE);
        // THE BratSession is deleted. modify BratViualizer to include windowSize in its state.
        // HttpSession session = BratSession.session();
        // Project project = (Project) session.getAttribute("project");
        // SourceDocument document = (SourceDocument) session.getAttribute("document");
        // int windowSize = (Integer)session.getAttribute("windowSize-" + project.getName()
        // +"-"+document.getName());
        // If this Classe is used somewhere, get BratAnnotatorModel populated somewhere
        BratAnnotatorModel bratAnnotatorDataModel = new BratAnnotatorModel();
        SpanAdapter.renderTokenAndSentence(jCas, response, bratAnnotatorDataModel);
        // If POS annotation exist in CAS
        SpanAdapter.getPosAdapter().render(jCas, response, bratAnnotatorDataModel);
        ChainAdapter.getCoreferenceLinkAdapter().render(jCas, response, bratAnnotatorDataModel);
        // If Lemma Layer Exist in CAS
        SpanAdapter.getLemmaAdapter().render(jCas, response, bratAnnotatorDataModel);
        // IF Named Entity layer exist in CAS
        SpanAdapter.getNamedEntityAdapter().render(jCas, response, bratAnnotatorDataModel);
        ArcAdapter.getDependencyAdapter().render(jCas, response, bratAnnotatorDataModel);
        ChainAdapter.getCoreferenceChainAdapter().render(jCas, response, bratAnnotatorDataModel);

        // Serialize BRAT object model to JSON
        try {
            StringWriter out = new StringWriter();
            JsonGenerator jsonGenerator = jsonConverter.getObjectMapper().getJsonFactory()
                    .createJsonGenerator(out);
            jsonGenerator.writeObject(response);
            docData = out.toString();
        }
        catch (IOException e) {
            error(ExceptionUtils.getRootCauseMessage(e));
        }

        return docData;
    }
}
