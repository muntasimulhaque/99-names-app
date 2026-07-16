# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class io.github.muntasimulhaque.names99.data.** {
    *** Companion;
}
-keepclasseswithmembers class io.github.muntasimulhaque.names99.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
