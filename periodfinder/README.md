# source
preqReceiver 

#MacOS instructions for the "library load disallowed by system policy" issue:
Check if the COPASI binding is signed: 
   $codesign -vvvv libCopasiJava.jnilib
If it is not signed, check if the file is in MacOS quarantine. The output may include com.apple.quarantine
  $xattr libCopasiJava.jnilib
If the file is in quarantine remove it from the MacOS quarantine
   $xattr -d com.apple.quarantine  libCopasiJava.jnilib
 Double check the quarantine list to ensure it is not listed anymore:
   $xattr libCopasiJava.jnilib
