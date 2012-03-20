# Bugspot - Bug Prediction Algorithm
Inspired by the blog post of the Google Engineering Team: [Bug Prediction at Google](http://google-engtools.blogspot.com/2011/12/bug-prediction-at-google.html)

## Usage
Build using maven

<pre>
1. mvn package
2. cp default.properties your-project.properties
</pre>

Edit the properties file:

<pre>
repository.type=svn
repository.url=http://redmine.rubyforge.org/svn/trunk
repository.user=anonymous
respository.password=
# exclude files matching this regex
analyzer.exclude = test\/
# only include files matching this regex
analyzer.include = \.rb$

# filter chain
filters = com.github.r1j0.bugspot.filter.MessageFilter,\
com.github.r1j0.bugspot.filter.RedmineFilter

# filter commit messages 
filter.MessageFilter.commit = bug|fix(es)?|close(s|d)

# when a commit references a ticket, only consider those commits
# that are classified in the "Defect" tracker
filter.RedmineFilter.ticket = #(\\d+)
filter.RedmineFilter.base = http://www.redmine.org/issues/
filter.RedmineFilter.tracker = Defect
# if you need authentication
# filter.RedmineFilter.username = user
# filter.RedmineFilter.password = password
</pre>

Run the analyzer:
<pre>
java -jar target/BugSpot-0.0.1-SNAPSHOT.jar -c redmine.properties -r1:HEAD
</pre>
