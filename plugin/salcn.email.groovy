<STYLE>
BODY, TABLE, TD, TH, P {
  font-family:Verdana,Helvetica,sans serif;
  font-size:11px;
  color:black;
}
TD.titleBg { color:white; background-color:#418ac7; font-size:120% }
TD.test_passed { color:green; }
TD.test_failed { color:red; }
TD.test_total { color:blue; }
TD.console { font-family:Courier New; }
</STYLE>

<BODY>

<TABLE>
  <!-- /*<TR><TD align="right"><IMG SRC="${rooturl}static/e59dfe28/images/32x32/<%= build.result.toString() == 'SUCCESS' ? "blue.gif" : build.result.toString() == 'FAILURE' ? 'red.gif' : 'yellow.gif' %>" />
  </TD><TD valign="center"><B style="font-size: 200%;">BUILD ${build.result}</B></TD></TR>*/ -->
  <TR><TD>URL:</TD><TD><A href="${rooturl}${build.url}">${rooturl}${build.url}</A></TD></TR>
  <TR><TD>Project:</TD><TD>${project.name}</TD></TR>
  <!-- /*<TR><TD>Date:</TD><TD>${it.timestampString}</TD></TR>
  <TR><TD>Duration:</TD><TD>${build.durationString}</TD></TR>*/ -->
  <TR><TD>Cause:</TD><TD><% build.causes.each() { cause -> %> ${cause.shortDescription} <%  } %></TD></TR>
</TABLE>
<BR/>


<!-- JUnit TEMPLATE -->
<%
  def bHasUnit = false, iUnitPass = 0, iUnitFail = 0, iUnitSkip = 0, aUnitFailedCase = []
  def bHasOpa = false, iOpaPass = 0, iOpaFail = 0, iOpaSkip = 0, aOpaFailedCase = []
  def bHasGherkin = false, iGherkinPass = 0, iGherkinFail = 0, iGherkinSkip = 0, aGherkinFailedCase = []
  def sTestResultTitle
  def junitResultList = it.JUnitTestResult

  try{
    def cucumberTestResultAction = it.getAction("org.jenkinsci.plugins.cucumber.jsontestsupport.CucumberTestResultAction")
    junitResultList.add(cucumberTestResultAction.getResult())
  } catch(e){
    //cucumberTestResultAction not exist in this build
  }

  if(junitResultList.size() > 0){
    sTestResultTitle = junitResultList.first().displayName

    junitResultList.each{ junitResult ->
        junitResult.getChildren().each { packageResult ->
          if(packageResult.getName() =~ /(?i)unitTests?/ || packageResult.getName() =~ /(?i)indexUnit/){
            bHasUnit = true
            iUnitPass = packageResult.getPassCount()
            iUnitFail = packageResult.getFailCount()
            iUnitSkip = packageResult.getSkipCount()
            aUnitFailedCase = packageResult.getFailedTests()
          }else if(packageResult.getName() =~ /(?i)opaTests?/ || packageResult.getName() =~ /(?i)indexOpa/){
            bHasOpa = true
            iOpaPass = packageResult.getPassCount()
            iOpaFail = packageResult.getFailCount()
            iOpaSkip = packageResult.getSkipCount()
            aOpaFailedCase = packageResult.getFailedTests()
          }else if(packageResult.getName() =~ /(?i)opaTestsWithGherkin/ || packageResult.getName() =~ /(?i)indexOpaWithGherkin/){
            bHasGherkin = true
            iGherkinPass = packageResult.getPassCount()
            iGherkinFail = packageResult.getFailCount()
            iGherkinSkip = packageResult.getSkipCount()
            aGherkinFailedCase = packageResult.getFailedTests()
          }
        }
    }
  }
%>

<TABLE width="100%">
  <TR><TD class="titleBg" colspan="5"><B>${sTestResultTitle}</B></TD></TR>
  <% if(bHasUnit){ %>
        <TR>
          <TD><B>Unit Test</B></TD>
          <TD class="test_passed">Passed: ${iUnitPass} test(s)</TD>
          <TD class="test_failed">Failed: ${iUnitFail} test(s)</TD>
          <TD>Skipped: ${iUnitSkip} test(s)</TD>
          <TD class="test_total">Total: ${iUnitPass+iUnitFail+iUnitSkip} test(s)</TD>
          <% aUnitFailedCase.each{ failed_test -> %>
                <TR><TD class="test_failed" colspan="5"><li>Failed: ${failed_test.getName()}</li></TD><TR>
          <% } %>
        </TR>
  <% }
     if(bHasOpa){ %>
        <TR>
          <TD><B>OPA Test</B></TD>
          <TD class="test_passed">Passed: ${iOpaPass} test(s)</TD>
          <TD class="test_failed">Failed: ${iOpaFail} test(s)</TD>
          <TD>Skipped: ${iOpaSkip} test(s)</TD>
          <TD class="test_total">Total: ${iOpaPass+iOpaFail+iOpaSkip} test(s)</TD>
          <% aOpaFailedCase.each{ failed_test -> %>
                <TR><TD class="test_failed" colspan="5"><li>Failed: ${failed_test.getName()}</li></TD></TR>
          <% } %>
        </TR>
  <% } 
    if(bHasGherkin){ %>
          <TR>
            <TD><B>Gherkin Test</B></TD>
            <TD class="test_passed">Passed: ${iGherkinPass} test(s)</TD>
            <TD class="test_failed">Failed: ${iGherkinFail} test(s)</TD>
            <TD>Skipped: ${iGherkinSkip} test(s)</TD>
            <TD class="test_total">Total: ${iGherkinPass+iGherkinFail+iGherkinSkip} test(s)</TD>
            <% aGherkinFailedCase.each{ failed_test -> %>
                  <TR><TD class="test_failed" colspan="5"><li>Failed: ${failed_test.getName()}</li></TD></TR>
            <% } %>
          </TR>
    <% }
     if(!bHasUnit && !bHasOpa && !bHasGherkin){ %>
          <TR>
              <TD colspan="5">No Test Case</TD>
          </TR>
  <% } %>
</TABLE>
<BR/>


<!-- CHANGE SET -->
<% def changeSet = build.changeSet
if(changeSet != null) {
        def hadChanges = false %>
        <TABLE width="100%">
    <TR><TD class="titleBg" colspan="2"><B>Changes</B></TD></TR>
<%      changeSet.each() { cs ->
                hadChanges = true %>
      <TR>
        <TD colspan="2">&nbsp;&nbsp;Revision <B><%= cs.metaClass.hasProperty('commitId') ? cs.commitId : cs.metaClass.hasProperty('revision') ? cs.revision :
        cs.metaClass.hasProperty('changeNumber') ? cs.changeNumber : "" %></B> by
          <B><%= cs.author %>: </B>
          <B>(${cs.msgAnnotated})</B>
         </TD>
      </TR>
<%              cs.affectedFiles.each() { p -> %>
        <TR>
          <TD width="10%">&nbsp;&nbsp;${p.editType.name}</TD>
          <TD>${p.path}</TD>
        </TR>
<%              }
        }

        if(!hadChanges) { %>
        <TR><TD colspan="2">No Changes</TD></TR>
<%      } %>
  </TABLE>
<BR/>
<% } %>


<!-- CONSOLE OUTPUT -->
<% if(build.result==hudson.model.Result.FAILURE) { %>
<TABLE width="100%" cellpadding="0" cellspacing="0">
<TR><TD class="bg1"><B>Console Output</B></TD></TR>
<%      build.getLog(100).each() { line -> %>
        <TR><TD class="console">${org.apache.commons.lang.StringEscapeUtils.escapeHtml(line)}</TD></TR>
<%      } %>
</TABLE>
<BR/>
<% } %>

</BODY>
