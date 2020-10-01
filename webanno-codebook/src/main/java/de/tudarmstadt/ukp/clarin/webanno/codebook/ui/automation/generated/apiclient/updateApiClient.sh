#!/bin/bash

echo "deleting old java files"
fd -e java -tf -X rm -r

echo "deleting old directories"
fd -td -X rm -r

echo "copying new client"
cp -r ~/downloads/api_client/src/main/java/io/swagger/client/* .

echo "fixing package"
fd -e java -x sed -i 's/io.swagger.client/de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient/g' {}

#echo "add Serializable to Models"
#echo "adding licence headers"
#read -d '' licence << EOF
#{§
# § Copyright 2020
# § Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
# § Technische Universität Darmstadt
# §
# § Licensed under the Apache License, Version 2.0 (the "License");
# § you may not use this file except in compliance with the License.
# § You may obtain a copy of the License at
# §
# §  http://www.apache.org/licenses/LICENSE-2.0
# §
# § Unless required by applicable law or agreed to in writing, software
# § distributed under the License is distributed on an "AS IS" BASIS,
# § WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# § See the License for the specific language governing permissions and
# § limitations under the License.
# §{
#EOF
#fd -e java -x sed -i "1s|^|${licence}|" {}

echo "fixing generated annotation" #TODO: remove javax.anno..... problem: include the import statement!
fd -e java -x sed -i 's/\@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date =/\@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",\n\t\tdate =/g'
