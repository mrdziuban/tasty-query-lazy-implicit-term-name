# [`tasty-query` issue #446](https://github.com/scalacenter/tasty-query/issues/446)

This repository reproduces [an issue with `tasty-query`](https://github.com/scalacenter/tasty-query/issues/446) where a
lazy implicit results in an error:

```
tastyquery.Exceptions$TastyFormatException: Expected TermName but got example.Test[$]._$_$
```

## Running

To run the test, clone this repo and run `sbt Test/run`.

## Details

The setup code is contained within [`Test.scala`](src/main/scala/example/Test.scala):

- `trait Foo` provides one `given` instance that requires a lazy/by-name instance of `Foo` for the inverse of the given type parameters
- `object Test` includes a call to `summon` an instance of `Foo[Int, String]`

The result of the `summon` call in `object Test` is a bit odd as it recursively summons instances of `Foo` to satisfy
the inverse relationship constraint, i.e. the resulting instance is:

```scala
Foo.inverse[Int, String](
  Foo.inverse[String, Int](
    Foo.inverse[Int, String](... to infinity ...)
  )
)
```

Using this setup code,  [`TastyQueryTest.scala`](src/test/scala/example/TastyQueryTest.scala) then initializes a
`tasty-query` context and tries to get the symbol of `example.Test.foo`. This causes the error:

```
tastyquery.Exceptions$TastyFormatException: Expected TermName but got example.Test[$]._$_$
  at tastyquery.reader.tasties.TastyUnpickler$NameTable.simple(TastyUnpickler.scala:46)
  at tastyquery.reader.tasties.TastyUnpickler.readName(TastyUnpickler.scala:95)
  at tastyquery.reader.tasties.TastyUnpickler.readUnsignedName(TastyUnpickler.scala:97)
  at tastyquery.reader.tasties.TastyUnpickler.$anonfun$1(TastyUnpickler.scala:154)
  at tastyquery.reader.tasties.TastyReader.until(TastyReader.scala:141)
  at tastyquery.reader.tasties.TastyUnpickler.readNameContents(TastyUnpickler.scala:154)
  at tastyquery.reader.tasties.TastyUnpickler.$init$$$anonfun$1(TastyUnpickler.scala:191)
  at tastyquery.reader.tasties.TastyUnpickler.$init$$$anonfun$adapted$1(TastyUnpickler.scala:191)
  at tastyquery.reader.tasties.TastyReader.until(TastyReader.scala:141)
  at tastyquery.reader.tasties.TastyUnpickler.<init>(TastyUnpickler.scala:191)
  at tastyquery.reader.tasties.TastyUnpickler.<init>(TastyUnpickler.scala:90)
  at tastyquery.reader.Loaders$PackageLoadingInfo.doLoadTasty(Loaders.scala:173)
  at tastyquery.reader.Loaders$PackageLoadingInfo.tryLoadRoot(Loaders.scala:107)
  at tastyquery.reader.Loaders$PackageLoadingInfo.loadOneRoot$$anonfun$1(Loaders.scala:75)
  at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
  at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
  at tastyquery.reader.Loaders$PackageLoadingInfo.loadingRoots(Loaders.scala:92)
  at tastyquery.reader.Loaders$PackageLoadingInfo.loadOneRoot(Loaders.scala:73)
  at tastyquery.Symbols$.tastyquery$Symbols$PackageSymbol$$_$getDecl$$anonfun$3$$anonfun$1(Symbols.scala:1900)
  at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
  at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
  at tastyquery.Symbols$PackageSymbol.loadingNewRoots(Symbols.scala:1876)
  at tastyquery.Symbols$PackageSymbol.getDecl$$anonfun$3(Symbols.scala:1900)
  at scala.Option.orElse(Option.scala:477)
  at tastyquery.Symbols$PackageSymbol.getDecl(Symbols.scala:1902)
  at tastyquery.Symbols$PackageSymbol.getDecl(Symbols.scala:1911)
  at tastyquery.Contexts$Context.loop$1(Contexts.scala:203)
  at tastyquery.Contexts$Context.findStaticOwner(Contexts.scala:214)
  at tastyquery.Contexts$Context.findStaticOwnerAndName(Contexts.scala:194)
  at tastyquery.Contexts$Context.findStaticTerm(Contexts.scala:182)
  at example.TastyQueryTest$.main(TastyQueryTest.scala:13)
  at example.TastyQueryTest.main(TastyQueryTest.scala)
  at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
  at java.base/java.lang.reflect.Method.invoke(Method.java:580)
```
