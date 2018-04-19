script_file = getClass().protectionDomain.codeSource.location.path
println("Current script: " + script_file)
println("Changing the hudson.model.DirectoryBrowserSupport.CSP setting.")
println("OLD hudson.model.DirectoryBrowserSupport.CSP=" + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP"))
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-scripts allow-same-origin; default-src 'none'; img-src 'self' data: ; style-src 'self' 'unsafe-inline' data: ; script-src 'self' 'unsafe-inline' 'unsafe-eval' ;")
println("NEW hudson.model.DirectoryBrowserSupport.CSP=" + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP"))