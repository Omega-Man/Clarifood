# Clarifood
Clarifood was created for Dubhacks 2016

Clarifood consists of an app and a server. The app allows the user to take pictures of food, before using the Clarifai API to determine what is present in the image. This list of items is then sent to the server, which checks the food present in the image against a table of recipes, and returns all recipies that can be made using only the food present in the original image.

Had there been more time to work on the project, the main priority would have been increasing the size of the server's "recipe book" without resorting to manual entry. This could be accomplished by crawling the web for recipies to be automatically entered into the table, accompanied by a link to the directions that could be provided to the user.
