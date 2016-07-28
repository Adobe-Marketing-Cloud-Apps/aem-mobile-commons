# aem.mobile.commons AEM Mobile Project

This a content package project generated using the AEM Multimodule Lazybones template.

## Building

This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.

## Using with AEM Developer Tools for Eclipse

To use this project with the AEM Developer Tools for Eclipse, import the generated Maven projects via the Import:Maven:Existing Maven Projects wizard. Then enable the Content Package facet on the _content_ project by right-clicking on the project, then select Configure, then Convert to Content Package... In the resulting dialog, select _src/main/content_ as the Content Sync Root.

## Using with VLT

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.

## Specifying CRX Host/Port

The CRX host and port can be specified on the command line with:
mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>

## Content Sync Configuration

Please make sure you copy the nodes under /etc/contentsync/tempates/aem-mobile-commons under your contentsync template.

## We Pharma Sales

**You need to have the we-healthcare-sales package installed before proceeding.**

 It can be obtained following the AEM Mobile [End to End Demo](https://internal.adobedemo.com/content/demo-hub/en/demos/external/wesellpharma-demo.html)

After installing the code, please install the aem-mobile-commons-we-pharma-sales-<version>.zip package.
This pacakge will give you some sample page components and templates to be used with the we-healthcare-sales project.

To use, create a page in AEM with the template "weSellPharma Test Latest Innovations"

