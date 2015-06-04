# gcm.js

**UPDATE:** This module is not maintained since more than 2 years ago. BTW, as it seems a lot of people is using it, some folks asked me to update it (for **Ti SDK 4.0**), no fork is improving it and Appcelerator has no intention to expose GCM stuff in its SDK -I cannot be more disappointed-, I'll try to find some time to do it. I quit Titanium development long before I stopped working on this module, since then I'm doing pure native development both iOS and Android (which is something I recommend 100%), so this is somehow is going to be quite backward. Also I'm quite busy... no time to make this a paid module and try to offer some support, so I think the best and fair solution to support the module development is make it **donationware**. So, here it is:

<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
<img alt="" border="0" src="https://www.paypalobjects.com/es_ES/i/scr/pixel.gif" width="1" height="1">
</form>


**gcm.js** is an open source module for Titanium Android SDK that lets developers receive GCM push notifications in their Android apps.
It has a very simple API -almost identical to iOS!- yet flexible and powerful, as it executes Javascript whenever a notification is received, no matter if the app is in foreground or background. More info at:
[http://iamyellow.net/post/40100981563/gcm-appcelerator-titanium-module](http://iamyellow.net/post/40100981563/gcm-appcelerator-titanium-module)

## Forking the module

If you want to fork and compile this module, first rename manifest_template file to manifest.
Then fill the guid (line #16) with a new module uuid with the **uuidgen** command as [this post](http://developer.appcelerator.com/blog/2011/09/module-verification.html) explains.

## Author

jordi domenech, [iamyellow.net](http://iamyellow.net)

## License

Apache License, Version 2.0
