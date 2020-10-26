#!/bin/bash

echo "deleting old java files"
fd -e java -tf -X rm -r

echo "deleting old directories"
fd -td -X rm -r

echo "copying new client"
unzip ~/downloads/java-client-generated.zip -d /tmp/api_client
cp -r /tmp/api_client/src/main/java/io/swagger/client/* .

echo "fixing package"
fd -e java -x sed -i 's/io.swagger.client/de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient/g' {}

echo "adding licence headers"
read -d '' licence <<EOF
/*
 * Copyright 2020
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
 */
EOF
# first, remove old header (12 lines)
fd -e java -x sed -i -e 1,12d {}
# add licence on top
fd -j 1 -e java -x bash -c "echo '$licence' | cat - {} > /tmp/out && mv /tmp/out {}"
