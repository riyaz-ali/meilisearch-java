<h1 align="center">MeiliSearch Java</h1>

<h4 align="center">
  <a href="https://github.com/meilisearch/MeiliSearch">MeiliSearch</a> |
  <a href="https://www.meilisearch.com">Website</a> |
  <a href="https://blog.meilisearch.com">Blog</a> |
  <a href="https://twitter.com/meilisearch">Twitter</a> |
  <a href="https://docs.meilisearch.com">Documentation</a> |
  <a href="https://docs.meilisearch.com/faq">FAQ</a>
</h4>

<p align="center">
  <a href="https://github.com/riyaz-ali/meilisearch-java/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-MIT-informational" alt="License"></a>
  <a href="https://slack.meilisearch.com"><img src="https://img.shields.io/badge/slack-MeiliSearch-blue.svg?logo=slack" alt="Slack"></a>
</p>

<p align="center">⚡ Lightning Fast, Ultra Relevant, and Typo-Tolerant Search Engine MeiliSearch client written in Java</p>

**MeiliSearch Java** is a client for **MeiliSearch** written in Java. **MeiliSearch** is a powerful, fast, open-source, easy to use and deploy search engine. Both searching and indexing are highly customizable. Features such as typo-tolerance, filters, and synonyms are provided out-of-the-box.

## Table of Contents <!-- omit in toc -->

- [🔧 Installation](#-installation)
- [🚀 Getting started](#-getting-started)
- [🤖 Compatibility with MeiliSearch](#-compatibility-with-meilisearch)
- [🎬 Examples](#-examples)
  - [Indexes](#indexes)
  - [Documents](#documents)
  - [Update status](#update-status)
  - [Search](#search)

## 🔧 Installation

```groovy
dependencies {
  // add meilisearch core package
  implementation 'net.riyazali.meilisearch-java:meili:master-SNAPSHOT'
  
  // add default remote and encoder packages (or you could also provide custom implementations!)
  implementation 'net.riyazali.meilisearch-java:meili-remote-okhttp:master-SNAPSHOT'
  implementation 'net.riyazali.meilisearch-java:meili-encoder-gson:master-SNAPSHOT'
}
```

### Run MeiliSearch <!-- omit in toc -->

There are many easy ways to [download and run a MeiliSearch instance](https://docs.meilisearch.com/guides/advanced_guides/installation.html#download-and-launch).

For example, if you use Docker:
```bash
$ docker run -it --rm -p 7700:7700 getmeili/meilisearch:latest ./meilisearch --master-key=masterKey
```

NB: you can also download MeiliSearch from **Homebrew** or **APT**.

## 🚀 Getting started

```java
public final class Example {
  
  public static void main(String args[]) throws Exception {
    Meili client = new Meili(
        // uses the default okhttp remote and gson encoder
        HttpRemote.create("https://meili.example.com:7700", "API_KEY"),
        GsonEncoder.create()
    );
    
    // get or create an index
    Index<Movie> index = client.index(Movie.class);
    
    // insert new documents
    Update op = index.insert(new Movie(/*...*/), new Movie(/*...*/));
    
    // add/update/delete operations return an update object 
    // which represents an asynchromous task queued on the meili server
    // use the returned object to query for the operation's status
    // see https://docs.meilisearch.com/guides/advanced_guides/asynchronous_updates.html for more
    
    // to wait until the update is applied
    while(!op.done()) {
      Thread.sleep(1_000);
      op = op.refresh();
    }
    
    // to lookup / search an index
    SearchPage<Movie> result = index.search("harry pottre"); // meili is typo-tolerant!
    for(Movie movie : result) {
      // ... cool stuff here ...
    }
  }
  
  // just add @Document to your model class and you're good to go!
  @Document(index = "movies", primaryKey = "id")
  static class Movie {
    private String id;
    // other fields omitted for brevity...
  }
}
```

## 🤖 Compatibility with MeiliSearch

This package is compatible with the following MeiliSearch versions:
- `v0.10.X`

## 🎬 Examples

Most of the HTTP routes of MeiliSearch are accessible via methods in this SDK.  
You can check out [the API documentation](https://docs.meilisearch.com/references/).

### Indexes

#### Get / create an index <!-- omit in toc -->

```java
// index creation is handled automatically by the sdk
Index<Movie> index = client.index(Movie.class);  // this will automatically create an index if one doesn't exist

// auto creation (while handy) incurs additional round-trip
// if you don't want that, you can use the following signature
Index<Movie> index = client.index(Movie.class, false /* autoCreate */);
```

### Documents

#### Fetch documents <!-- omit in toc -->

```java
// use the document's primary key to fetch an instance
Movie movie = index.get("1234");
```

#### Add documents <!-- omit in toc -->

```java
// add a single document
Update op = index.insert(new Movie(/* ... */));

// or many documents at once
Update op = index.insert(new Movie(/* .. */), new Movie(/* .. */));
```

#### Delete documents <!-- omit in toc -->

```go
// Delete one document
index.delete(movie);

// Delete several documents
index.delete(movie0, movie1, movie2);

// Delete all documents /!\
index.clear();
```

### Search

#### Basic search <!-- omit in toc -->

```java
SearchPage<Movie> result = index.search("prince");
```

#### Custom search <!-- omit in toc -->

All the supported options are described in [this documentation section](https://docs.meilisearch.com/references/search.html#search-in-an-index).

```java
SearchPage<Movie> result = index.search(
    SearchConfig.builder().query("harry pottre").highlight("title").build()
);
```
