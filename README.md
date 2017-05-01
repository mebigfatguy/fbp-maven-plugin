# fbp-maven-plugin
a plugin that generates FindBugs project files (fbp) from a maven project

To use:
1) download this jar to your local maven repository.
2) edit your settings.xml and add

  	<pluginGroups>
	      <pluginGroup>com.mebigfatguy.fbp-maven-plugin</pluginGroup>
	  </pluginGroups>
3) run mvn fbp:fbp
