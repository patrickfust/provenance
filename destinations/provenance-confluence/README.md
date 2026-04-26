# Confluence

If you add this module, you can send documentation to Confluence.

## ConfluenceDestination

Class name: `dk.fust.provenance.destination.confluence.destination.ConfluenceDestination`

### Setup
In order to use Confluence as destination, you'll need to add this module as a dependency:
```groovy
buildscript {
    dependencies {
        classpath("dk.fust.provenance:provenance-gradle-plugin:${provenanceVersion}")
        classpath("dk.fust.provenance:provenance-destination-confluence:${provenanceVersion}")
    }
}
```

### Configuration of destination

| Setting             | Type   | Description             | Example                                 |
|---------------------|--------|-------------------------|-----------------------------------------|
| baseUrl             | String | Base URL to Confluence. | https://xxx.atlassian.net/wiki/rest/api |
| documentationPageId | String | Id of the parent page.  | 123456                                  |
| spaceKey            | String | Confluence space key    | ABC                                     |
| parentPageTitle     | String |                         | My Parent Page                          |
| pageTitle           | String |                         | My Page                                 | 

Username and personal access token for Confluence will be prompted.
