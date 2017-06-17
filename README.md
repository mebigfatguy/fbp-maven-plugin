# fbp-maven-plugin
a plugin that generates FindBugs project files (fbp) from a maven project

To use:
1. download this jar to your local maven repository.
1. edit your settings.xml and add
```
  	<pluginGroups>
	      <pluginGroup>com.mebigfatguy.fbp-maven-plugin</pluginGroup>
	 </pluginGroups>
```
1. run mvn fbp:fbp


### Available at maven central with coordinates ###

         GroupId: com.mebigfatguy.fbp-maven-plugin
        ArtifactId: fbp-maven-plugin
           Version: 0.4.1