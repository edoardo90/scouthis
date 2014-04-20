<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta http-equiv="Content-Style-Type" content="text/css">
        <meta http-equiv="Content-Script-Type" content="text/javascript">

        <title>TurnKey LAMP</title>
        
        <link rel="stylesheet" href="css/ui.tabs.css" type="text/css" media="print, projection, screen"/>
        <link rel="stylesheet" href="css/base.css" type="text/css"/>

        
    </head>

    <body>
        
        <h1>TurnKey LAMP</h1>
        <?php
			// prints e.g. 'Current PHP version: 4.1.1'
			echo 'Current PHP version: ' . phpversion();

			// prints e.g. '2.0' or nothing if the extension isn't enabled
			echo phpversion('tidy');
		?>
    </body>
</html>
