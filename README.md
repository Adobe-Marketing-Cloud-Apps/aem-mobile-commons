# AEM Mobile Commons Project

## Dependencies

- [ACS AEM Commons (v. 3.0.2)](https://adobe-consulting-services.github.io/acs-aem-commons)
- we.Pharma Sales

### We Pharma Sales

**You need to have the we-healthcare-sales package installed before proceeding.**

It can be obtained by following the AEM Mobile [End to End Demo](https://internal.adobedemo.com/content/demo-hub/en/demos/external/wesellpharma-demo.html)

After installing the code, please install the `aem-mobile-commons-we-pharma-sales-<version>.zip` package.
This pacakge will give you some sample page components and templates to be used with the we-healthcare-sales project.

To use, create a page in AEM with the template `weSellPharma Test Latest Innovations`.

## Components

The following components are included with this project.

### Lightbox

[Source](content/src/main/content/jcr_root/apps/aem-mobile-commons/components/content/lightbox)

### Slideshow

[Source](content/src/main/content/jcr_root/apps/aem-mobile-commons/components/content/slideshow)

### Video

[Source](content/src/main/content/jcr_root/apps/aem-mobile-commons/components/content/video)

## Building

This project uses Maven for building. Common commands:

From the root directory, run `mvn -PautoInstallPackage clean install` to build the bundle and content package and install to an AEM instance.

From the bundle directory, run `mvn -PautoInstallBundle clean install` to build *just* the bundle and install to an AEM instance.

### Using with VLT

To use vlt with this project, first build and install the package to your local AEM instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal `vlt up` and `vlt ci` commands.

### Specifying CRX Host/Port

The CRX host and port can be specified on the command line with:
`mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>`

## Content Sync Configuration

Please make sure you copy the nodes under `/etc/contentsync/tempates/aem-mobile-commons` under your contentsync template.
