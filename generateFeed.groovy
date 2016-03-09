@Grab('com.rometools:rome:1.5.0')
@Grab('org.jsoup:jsoup:1.8.2')
import com.rometools.rome.feed.synd.SyndCategory
import com.rometools.rome.feed.synd.SyndCategoryImpl
import com.rometools.rome.feed.synd.SyndContent
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedOutput
import groovy.io.FileType
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.text.DateFormat
import java.text.SimpleDateFormat

def conf = new JsonSlurper().parse(new File('hubpress/config.json'))

SyndFeed feed = new SyndFeedImpl()
feed.with {
    title = conf.site.title
    link = conf.site.url
    description = conf.site.description
    feedType = 'rss_2.0'
    author = conf.socialnetwork.email
    publishedDate = new Date()
    language = 'en-UK'
}

List<SyndEntry> entries = []
DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
df.setTimeZone(TimeZone.getTimeZone('GMT-2'))

new File('2015').eachFileRecurse(FileType.FILES) { File post ->
    SyndEntry entry = new SyndEntryImpl()
    Document document = Jsoup.parse(post, 'UTF-8')

    SyndContent syndDescription = new SyndContentImpl()
    syndDescription.value = document.getElementsByTag('meta').find { it.attr('property') == 'og:description' }.attr('content')

    entry.with {
        title = document.getElementsByTag('meta').find { it.attr('property') == 'og:title' }.attr('content')
        link = document.getElementsByTag('meta').find { it.attr('property') == 'og:url' }.attr('content')
        description = syndDescription
        publishedDate = df.parse(document.getElementsByTag('meta').find { it.attr('property') == 'article:published_time' }.attr('content'))
        categories = document.getElementsByTag('meta').find { it.attr('property') == 'article:tag' }.attr('content').tokenize('').collect {String tag ->
            SyndCategory category = new SyndCategoryImpl()
            category.name = tag
            return category
        }
    }

    entries << entry
}

new File('2016').eachFileRecurse(FileType.FILES) { File post ->
    SyndEntry entry = new SyndEntryImpl()
    Document document = Jsoup.parse(post, 'UTF-8')

    SyndContent syndDescription = new SyndContentImpl()
    syndDescription.value = document.getElementsByTag('meta').find { it.attr('property') == 'og:description' }.attr('content')

    entry.with {
        title = document.getElementsByTag('meta').find { it.attr('property') == 'og:title' }.attr('content')
        link = document.getElementsByTag('meta').find { it.attr('property') == 'og:url' }.attr('content')
        description = syndDescription
        publishedDate = df.parse(document.getElementsByTag('meta').find { it.attr('property') == 'article:published_time' }.attr('content'))
        categories = document.getElementsByTag('meta').find { it.attr('property') == 'article:tag' }.attr('content').tokenize('').collect {String tag ->
            SyndCategory category = new SyndCategoryImpl()
            category.name = tag
            return category
        }
    }

    entries << entry
}

feed.entries = entries

Writer writer = new FileWriter('rss');
SyndFeedOutput output = new SyndFeedOutput()
output.output(feed,writer)
writer.close()