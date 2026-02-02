-------------------------------------------------------------------------------
Test set: com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest
-------------------------------------------------------------------------------
Tests run: 2, Failures: 0, Errors: 2, Skipped: 0, Time elapsed: 1.099 s <<< FAILURE! -- in com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest
com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest.rejectTask_ShouldMapToPutAndExtractTaskId -- Time elapsed: 1.016 s <<< ERROR!
org.mockito.exceptions.base.MockitoException: 

Mockito cannot mock this class: class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController.

If you're not sure why you're getting this error, please open an issue on GitHub.


Java               : 17
JVM vendor name    : Eclipse Adoptium
JVM vendor version : 17.0.9+9
JVM name           : OpenJDK 64-Bit Server VM
JVM version        : 17.0.9+9
JVM info           : mixed mode, sharing
OS name            : Windows 11
OS version         : 10.0


You are seeing this disclaimer because Mockito is configured to create inlined mocks.
You can learn about inline mocks and their limitations under item #39 of the Mockito class javadoc.

Underlying exception : org.mockito.exceptions.base.MockitoException: Could not modify all classes [class com.company.hrms.workflow.api.controller.HR11WorkflowCmdController, class java.lang.Object, class com.company.hrms.common.controller.CommandBaseController, class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController]
	at com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest.setup(WorkflowCommandControllerTest.java:27)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
Caused by: org.mockito.exceptions.base.MockitoException: Could not modify all classes [class com.company.hrms.workflow.api.controller.HR11WorkflowCmdController, class java.lang.Object, class com.company.hrms.common.controller.CommandBaseController, class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController]
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:168)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:399)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:190)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:410)
	... 4 more
Caused by: java.lang.IllegalStateException: 
Byte Buddy could not instrument all classes within the mock's type hierarchy

This problem should never occur for javac-compiled classes. This problem has been observed for classes that are:
 - Compiled by older versions of scalac
 - Classes that are part of the Android distribution
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.triggerRetransformation(InlineBytecodeGenerator.java:285)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.mockClass(InlineBytecodeGenerator.java:218)
	at org.mockito.internal.creation.bytebuddy.TypeCachingBytecodeGenerator.lambda$mockClass$0(TypeCachingBytecodeGenerator.java:78)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:168)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:399)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:190)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:410)
	at org.mockito.internal.creation.bytebuddy.TypeCachingBytecodeGenerator.mockClass(TypeCachingBytecodeGenerator.java:75)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.createMockType(InlineDelegateByteBuddyMockMaker.java:412)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.doCreateMock(InlineDelegateByteBuddyMockMaker.java:371)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.createSpy(InlineDelegateByteBuddyMockMaker.java:361)
	at org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker.createSpy(InlineByteBuddyMockMaker.java:62)
	at org.mockito.internal.util.MockUtil.createMock(MockUtil.java:91)
	at org.mockito.internal.MockitoCore.mock(MockitoCore.java:88)
	at org.mockito.Mockito.spy(Mockito.java:2188)
	... 4 more
Caused by: java.lang.NoClassDefFoundError: ApproveTaskRequest
	at java.base/java.lang.Class.getDeclaredMethods0(Native Method)
	at java.base/java.lang.Class.privateGetDeclaredMethods(Class.java:3402)
	at java.base/java.lang.Class.getDeclaredMethods(Class.java:2504)
	at net.bytebuddy.description.method.MethodList$ForLoadedMethods.<init>(MethodList.java:152)
	at net.bytebuddy.description.type.TypeDescription$ForLoadedType.getDeclaredMethods(TypeDescription.java:8940)
	at org.mockito.internal.creation.bytebuddy.MockMethodAdvice$ConstructorShortcut.wrap(MockMethodAdvice.java:400)
	at net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$Entry.wrap(AsmVisitorWrapper.java:575)
	at net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$DispatchingVisitor.visitMethod(AsmVisitorWrapper.java:657)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default$ForInlining$WithFullProcessing$RedefinitionClassVisitor.onVisitMethod(TypeWriter.java:5184)
	at net.bytebuddy.utility.visitor.MetadataAwareClassVisitor.visitMethod(MetadataAwareClassVisitor.java:302)
	at net.bytebuddy.jar.asm.ClassReader.readMethod(ClassReader.java:1354)
	at net.bytebuddy.jar.asm.ClassReader.accept(ClassReader.java:745)
	at net.bytebuddy.jar.asm.ClassReader.accept(ClassReader.java:425)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default$ForInlining.create(TypeWriter.java:4014)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default.make(TypeWriter.java:2224)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase$UsingTypeWriter.make(DynamicType.java:4055)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase.make(DynamicType.java:3739)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.transform(InlineBytecodeGenerator.java:402)
	at java.instrument/java.lang.instrument.ClassFileTransformer.transform(ClassFileTransformer.java:244)
	at java.instrument/sun.instrument.TransformerManager.transform(TransformerManager.java:188)
	at java.instrument/sun.instrument.InstrumentationImpl.transform(InstrumentationImpl.java:541)
	at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses0(Native Method)
	at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses(InstrumentationImpl.java:169)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.triggerRetransformation(InlineBytecodeGenerator.java:281)
	... 18 more
Caused by: java.lang.ClassNotFoundException: ApproveTaskRequest
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
	... 42 more

com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest.approveTask_ShouldMapToPutAndExtractTaskId -- Time elapsed: 0.036 s <<< ERROR!
org.mockito.exceptions.base.MockitoException: 

Mockito cannot mock this class: class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController.

If you're not sure why you're getting this error, please open an issue on GitHub.


Java               : 17
JVM vendor name    : Eclipse Adoptium
JVM vendor version : 17.0.9+9
JVM name           : OpenJDK 64-Bit Server VM
JVM version        : 17.0.9+9
JVM info           : mixed mode, sharing
OS name            : Windows 11
OS version         : 10.0


You are seeing this disclaimer because Mockito is configured to create inlined mocks.
You can learn about inline mocks and their limitations under item #39 of the Mockito class javadoc.

Underlying exception : org.mockito.exceptions.base.MockitoException: Could not modify all classes [class com.company.hrms.workflow.api.controller.HR11WorkflowCmdController, class java.lang.Object, class com.company.hrms.common.controller.CommandBaseController, class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController]
	at com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest.setup(WorkflowCommandControllerTest.java:27)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
Caused by: org.mockito.exceptions.base.MockitoException: Could not modify all classes [class com.company.hrms.workflow.api.controller.HR11WorkflowCmdController, class java.lang.Object, class com.company.hrms.common.controller.CommandBaseController, class com.company.hrms.workflow.api.controller.WorkflowCommandControllerTest$TestableController]
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:168)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:399)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:190)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:410)
	... 4 more
Caused by: java.lang.IllegalStateException: 
Byte Buddy could not instrument all classes within the mock's type hierarchy

This problem should never occur for javac-compiled classes. This problem has been observed for classes that are:
 - Compiled by older versions of scalac
 - Classes that are part of the Android distribution
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.triggerRetransformation(InlineBytecodeGenerator.java:285)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.mockClass(InlineBytecodeGenerator.java:218)
	at org.mockito.internal.creation.bytebuddy.TypeCachingBytecodeGenerator.lambda$mockClass$0(TypeCachingBytecodeGenerator.java:78)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:168)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:399)
	at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:190)
	at net.bytebuddy.TypeCache$WithInlineExpunction.findOrInsert(TypeCache.java:410)
	at org.mockito.internal.creation.bytebuddy.TypeCachingBytecodeGenerator.mockClass(TypeCachingBytecodeGenerator.java:75)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.createMockType(InlineDelegateByteBuddyMockMaker.java:412)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.doCreateMock(InlineDelegateByteBuddyMockMaker.java:371)
	at org.mockito.internal.creation.bytebuddy.InlineDelegateByteBuddyMockMaker.createSpy(InlineDelegateByteBuddyMockMaker.java:361)
	at org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker.createSpy(InlineByteBuddyMockMaker.java:62)
	at org.mockito.internal.util.MockUtil.createMock(MockUtil.java:91)
	at org.mockito.internal.MockitoCore.mock(MockitoCore.java:88)
	at org.mockito.Mockito.spy(Mockito.java:2188)
	... 4 more
Caused by: java.lang.NoClassDefFoundError: ApproveTaskRequest
	at java.base/java.lang.Class.getDeclaredMethods0(Native Method)
	at java.base/java.lang.Class.privateGetDeclaredMethods(Class.java:3402)
	at java.base/java.lang.Class.getDeclaredMethods(Class.java:2504)
	at net.bytebuddy.description.method.MethodList$ForLoadedMethods.<init>(MethodList.java:152)
	at net.bytebuddy.description.type.TypeDescription$ForLoadedType.getDeclaredMethods(TypeDescription.java:8940)
	at org.mockito.internal.creation.bytebuddy.MockMethodAdvice$ConstructorShortcut.wrap(MockMethodAdvice.java:400)
	at net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$Entry.wrap(AsmVisitorWrapper.java:575)
	at net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$DispatchingVisitor.visitMethod(AsmVisitorWrapper.java:657)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default$ForInlining$WithFullProcessing$RedefinitionClassVisitor.onVisitMethod(TypeWriter.java:5184)
	at net.bytebuddy.utility.visitor.MetadataAwareClassVisitor.visitMethod(MetadataAwareClassVisitor.java:302)
	at net.bytebuddy.jar.asm.ClassReader.readMethod(ClassReader.java:1354)
	at net.bytebuddy.jar.asm.ClassReader.accept(ClassReader.java:745)
	at net.bytebuddy.jar.asm.ClassReader.accept(ClassReader.java:425)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default$ForInlining.create(TypeWriter.java:4014)
	at net.bytebuddy.dynamic.scaffold.TypeWriter$Default.make(TypeWriter.java:2224)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase$UsingTypeWriter.make(DynamicType.java:4055)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase.make(DynamicType.java:3739)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.transform(InlineBytecodeGenerator.java:402)
	at java.instrument/java.lang.instrument.ClassFileTransformer.transform(ClassFileTransformer.java:244)
	at java.instrument/sun.instrument.TransformerManager.transform(TransformerManager.java:188)
	at java.instrument/sun.instrument.InstrumentationImpl.transform(InstrumentationImpl.java:541)
	at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses0(Native Method)
	at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses(InstrumentationImpl.java:169)
	at org.mockito.internal.creation.bytebuddy.InlineBytecodeGenerator.triggerRetransformation(InlineBytecodeGenerator.java:281)
	... 18 more
Caused by: java.lang.ClassNotFoundException: ApproveTaskRequest
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
	... 42 more

