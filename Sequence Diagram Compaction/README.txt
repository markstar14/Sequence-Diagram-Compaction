To include plantuml.jar into the Buildpath:

1. Create a lib directory in your project
2. Use Import-> File System to import the jar files into your plugin project
3. In plugin.xml->Runtime tab, use "Add..." to add the jars to the classpath section.
4. In the same dialog, use "New" to add "." (with no quotes for dot)
5. Go to plugin.xml->Build tab and check if the new jar file is in Binary build section.
6. Save plugin.xml
7. Right-click on the project, select Plugin-Tools -> Update classpath to correctly 
add the jars to the eclipse project classpath.