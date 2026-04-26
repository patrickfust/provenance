# Destinations

Destination is the where *Provenance* will send the provenance.
You can use these or implement your own.

## DirectoryDestination

Class name: `dk.fust.provenance.destination.DirectoryDestination`

Sends to separate files in the directory

| Setting                 | Type    | Description                                                     | Default |
|-------------------------|---------|-----------------------------------------------------------------|---------|
| directory               | File    | Where the files will be stored                                  |         |
| createParentDirectories | boolean | Should the directory's parent directories be created if missing | false   |

## FileDestination

Class name: `dk.fust.provenance.destination.FileDestination`

Replace an entire file with the document.

| Setting                 | Type    | Description                                                | Default |
|-------------------------|---------|------------------------------------------------------------|---------|
| file                    | File    | Location of the file. Must be writeable                    |         |
| createParentDirectories | boolean | Should the file's parent directories be created if missing | false   |

## Base64FileDestination

Class name: `dk.fust.provenance.destination.Base64FileDestination`

Base64 decodes the document and replaces the entire file with binary content.
Can for instance be used in conjunction with [ExcelBase64TableFormatter](#excelbase64tableformatter).


| Setting | Type | Description                             | Default |
|---------|------|-----------------------------------------|---------|
| file    | File | Location of the file. Must be writeable |         |

## MarkdownDestination

Class name: dk.fust.provenance.destination.MarkdownDestination

| Setting | Type | Description                 | Default |
|---------|------|-----------------------------|---------|
| file    | File | Markdown file to be updated |         |  

In order for the `MarkdownDestination` being able to substitute parts of a markdown-file,
you'll mark a start and an end placeholder, and everything in between will be substituted.

### Markup in Markdown
For instance this shows a substitution with the key `MY_KEY`, where the beginning and end is.
Everything in between will be substituted.

```markdown
[//]: #MY_KEY_START ()
... this is replaced ...
[//]: #MY_KEY_END ()
```

## ConfluenceDestination

Class name: `dk.fust.provenance.destination.confluence.destination.ConfluenceDestination`

In order to use Confluence as destination, you'll need to add the destination `provenance-destination-confluence`.

Read the provenance [here](../destinations/provenance-confluence)
