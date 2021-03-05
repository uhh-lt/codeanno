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
package de.uhh.lt.codeanno.ui.automation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import de.uhh.lt.codeanno.model.CodebookTag;

@Entity
@Table(name = "cba_tag_label_mapping", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "model_version", "label", "tag" }) })
public class TagLabelMapping
    implements Serializable
{
    private static final long serialVersionUID = 2146173427854120667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    @Column(name = "label", nullable = false)
    private String label;

    @ManyToOne
    @JoinColumn(name = "tag")
    @OnDelete(action = OnDeleteAction.CASCADE) // remove the mapping if the CB was deleted
    private CodebookTag tag;

    public TagLabelMapping()
    {
        // Required
    }

    public TagLabelMapping(String modelVersion, String label, CodebookTag tag)
    {
        this.modelVersion = modelVersion;
        this.label = label;
        this.tag = tag;
    }

    public Long getId()
    {
        return id;
    }

    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public CodebookTag getTag()
    {
        return tag;
    }

    public void setTag(CodebookTag tag)
    {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TagLabelMapping that = (TagLabelMapping) o;

        return new EqualsBuilder().append(modelVersion, that.modelVersion).append(label, that.label)
                .append(tag, that.tag).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(modelVersion).append(label).append(tag)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return "TagLabelMapping{" + "modelVersion='" + modelVersion + '\'' + ", label='" + label
                + '\'' + ", tag=" + tag + '}';
    }
}
