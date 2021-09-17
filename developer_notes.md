# Developer Tips & Tricks

## How to update the WebAnno base-version
1) Open `./pom.xml`
2) Change `/project/properties/webanno.version` to whatever is the latest (beta-snapshot) 
   version released [here](https://github.com/webanno/webanno/releases)
3) run `mvn clear verify -U`

## How to re-include a WebAnno only module
_This needs to be done if WebAnno code has to be modified for CodeAnno (but should be avoided if 
possible to keep the code clean)_

In the following, as an example, we re-include the `webanno-api` module. To re-include a 
different module, replace `webannp-api` with the respective module name in the following 
commands etc. 

### Make sure that WebAnno is configured as remote like this:
```
webanno git@github.com:webanno/webanno.git (fetch)
webanno git@github.com:webanno/webanno.git (push)
```
This can be done by `git remote add webanno git@github.com:webanno/webanno.git`

### Run the following commands to get the latest webanno code changes, and the module code in your local repo:
1) `git checkout webanno-master`
2) `git pull webanno master`
3) `git push`
4) `cp -r webanno-api webanno-api_copy`  
5) `git checkout -b webanno-api-include`
6) `rm -r webanno-api`
7) `mv webanno-api_copy webanno-api`

or for convenience run this:

```bash
git checkout webanno-master && \
git pull webanno master && \
git push && \
cp -r webanno-api webanno-api_copy && \
git checkout -b webanno-api-include main && \
rm -r webanno-api && \
mv webanno-api_copy webanno-api
```

### Re-include the respective module (e.g., the `webanno-api` module):

#### First, edit main `./pom.xml` as follows
1) in `/project/dependencyManagement` add
```xml
<dependency>
  <groupId>de.uhh.lt.codeanno</groupId>
  <artifactId>webanno-api</artifactId>
  <version>${project.version}</version>
</dependency>
```
2) in `/project/dependencyManagement` change this:
```xml
<dependency>
  <groupId>de.tudarmstadt.ukp.clarin.webanno</groupId>
  <artifactId>webanno-api</artifactId>
  <version>${webanno.version}</version>
</dependency>
```
to this:
```xml
<dependency>
  <groupId>de.tudarmstadt.ukp.clarin.webanno</groupId>
  <artifactId>webanno-api</artifactId>
  <version>${webanno.version}</version>
  <scope>provided</scope>
</dependency>
```
3) in `/project/modules` add `<module>webanno-api</module>`

#### Second, edit `./webanno-api/pom.xml`

1) in `/project/parent/` change this:
```xml
<parent>
  <groupId>de.tudarmstadt.ukp.clarin.webanno</groupId>
  <artifactId>webanno</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</parent>
```
to this:
```xml
<parent>
  <groupId>de.uhh.lt.codeanno</groupId>
  <artifactId>codeanno</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</parent>
```

2) change `<groupId>de.tudarmstadt.ukp.clarin.webanno</groupId>` to `<groupId>de.uhh.lt.codeanno</groupId>` from all dependency that are returned by running the following command
```bash
sed -e 's/xmlns="[^"]*"//g' pom.xml | \
xmllint --xpath "/project/dependencyManagement/dependencies/dependency[scope='provided']/artifactId" - | \
sed 's/></>\n</g' -
```

#### Lastly, verify with maven `mvn clean verify`
If everything is fine, you can now push the changes (should only be `./pom.xml`) and start 
amending the re-included `webanno-api` module.