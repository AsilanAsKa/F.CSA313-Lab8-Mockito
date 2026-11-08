# F.CSA313 — Лаборатори 8: Mockito

**Оюутны нэр:** С. Асилан  
**Оюутны код:** B232270115  
**Хичээл:** Программ хангамжийн тестлэлт  
**Сэдэв:** Mockito ашиглан Unit Test бичих

## 1. Зорилго

Энэхүү лабораторийн ажлаар Mockito сан ашиглан Java програмын Unit Test бичиж сурсан. Mock объект ашиглан `CourseRegistrationService` классын үйл ажиллагааг шалгасан.

## 2. Ашигласан технологи

- Java 21
- Apache Maven 3.8.7
- JUnit 5
- Mockito 5.11.0
- Ubuntu Linux

### Java хувилбар

```text
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-24.04.4-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-24.04.4-Ubuntu, mixed mode, sharing)
```

### Maven хувилбар

```text
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 21.0.12.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-42-generic", arch: amd64, family: "unix"
```

## 3. Хийж гүйцэтгэсэн ажил

`CourseRegistrationService` классын бүртгэлийн үйл ажиллагаанд тест бичсэн.

Нийт 8 төрлийн шалгалт хийсэн. A8 шалгалтад Parameterized Test ашиглан 3 өөр нөхцөлийг шалгасан. Ингэснээр нийт 10 тест ажилласан.

| Тест | Шалгасан нөхцөл |
|---|---|
| A1 | Идэвхтэй оюутан амжилттай бүртгүүлэх |
| A2 | Идэвхгүй оюутан бүртгүүлэх боломжгүй |
| A3 | Оюутан олдоогүй үед алдаа буцаах |
| A4 | Өмнөх холбоо хичээл дутуу үед бүртгэхгүй байх |
| A5 | Имэйл илгээхэд алдаа гарсан ч бүртгэл амжилттай байх |
| A6 | Null утга орж ирэхэд алдаа үүсгэх |
| A7 | ArgumentCaptor ашиглан имэйлийн агуулгыг шалгах |
| A8 | Parameterized Test ашиглан 3 алдааны нөхцөлийг шалгах |

## 4. Mockito ашигласан байдал

Mockito-ийн `@Mock` ашиглан `StudentRepository`, `EmailSender` объектуудыг Mock болгосон.

- `when().thenReturn()` ашиглан буцаах утгыг тохируулсан.
- `verify()` ашиглан функц дуудагдсан эсэхийг шалгасан.
- `never()` ашиглан тодорхой үйлдэл хийгдээгүй эсэхийг шалгасан.
- `verifyNoInteractions()` ашиглан объекттой харилцсан эсэхийг шалгасан.
- `doThrow()` ашиглан алдааны нөхцөлийг шалгасан.
- `ArgumentCaptor` ашиглан имэйлийн агуулгыг шалгасан.

## 5. lenient() ашигласан шалтгаан

A8 тестэд `@ParameterizedTest` болон `@CsvSource` ашиглан гурван өөр нөхцөлийг шалгасан. Зарим нөхцөлд нийтлэг stub ашиглагдахгүй байсан тул Mockito-ийн strict stubbing анхааруулга гарч байсан. Иймээс `lenient()` ашиглан тухайн stub ашиглагдаагүй үед алдаа үүсэхээс сэргийлсэн.

## 6. Тестийн үр дүн

`mvn clean test` командаар нийт 10 тест ажиллуулсан.

| Үзүүлэлт | Үр дүн |
|---|---:|
| Tests run | 10 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 0 |
| Build | SUCCESS |

Тестийн гаралт `results/mvn-test.txt` файлд хадгалагдсан.

## 7. Mutation Testing

Тестүүд алдааг илрүүлж чадаж байгаа эсэхийг шалгахын тулд үндсэн кодод гурван өөрчлөлт хийж туршсан.

| Мутант | Хийсэн өөрчлөлт | Унасан тест |
|---|---|---|
| 1 | Идэвхгүй оюутны шалгалтыг түр хассан | `inactiveStudent_returnsErrorAndDoesNotRegister`, `registrationErrors_returnExpectedResult[2]` |
| 2 | `saveRegistration()` дуудалтыг түр хассан | `activeStudent_registersSuccessfully_returnsOK`, `emailFailure_registrationStillSucceeds` |
| 3 | `courseId == null` нөхцөлийг хассан | `nullInput_throwsIllegalArgumentException` |

Мутант 1 болон 2 тус бүр 2 failure, мутант 3 нь 1 failure үүсгэсэн. Гурван мутант бүгд тестээр илэрч `BUILD FAILURE` болсон. Туршилт бүрийн дараа үндсэн кодыг сэргээсэн.

Үр дүнг `results/mvn-test-mutant-1.txt`, `results/mvn-test-mutant-2.txt`, `results/mvn-test-mutant-3.txt` файлуудад хадгалсан.

## 8. Дүгнэлт

Энэ лабораторийн ажлаар Mockito ашиглан хамаарлуудыг Mock объектоор орлуулж тестэлж сурлаа. Mock нь хүссэн үйлдлийг тохируулж, объектын функц дуудагдсан эсэхийг шалгадаг бол Fake нь бодит системийн хялбаршуулсан хувилбараар ажилладаг. Бүх объектыг Mock хийх шаардлагагүй бөгөөд энгийн өгөгдлийн объектуудыг боломжтой үед бодитоор нь ашиглах хэрэгтэй. `verify()`-г хэт их ашиглавал тест нь програмын үр дүнг биш, дотоод хэрэгжүүлэлтийг шалгадаг болж болно. Ингэснээр кодын бүтэц өөрчлөгдөхөд үйл ажиллагаа хэвийн байсан ч тест унах эрсдэлтэй. Mutation Testing хийснээр бичсэн тестүүд кодын алдааг илрүүлж байгааг шалгасан. Эцэст нь нийт 10 тестийг амжилттай ажиллуулж, үндсэн кодыг хэвийн байдалд сэргээсэн.
