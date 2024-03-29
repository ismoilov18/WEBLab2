# Вопросы с [se.ifmo.ru](https://se.ifmo.ru/courses/web)

## 1. Java-сервлеты. Особенности реализации, ключевые методы, преимущества и недостатки относительно CGI и FastCGI.

Пакеты `javax.servlet` и `javax.servlet.http` обеспечивают интерфейсы и классы для создания сервлетов.

__Cервлет__ — это Java-класс, который наследуется обычно от класса `HttpServlet` и переопределяет часть методов:

- `doGet` — если мы хотим, чтобы сервлет реагировал на GET запрос.
- `doPost` — если мы хотим, чтобы сервлет реагировал на POST запрос.
- `doPut`, `doDelete` — если мы хотим, чтобы сервлет реагировал на PUT и DELETE запрос (есть и такие в HTTP). Эти методы реализуются крайне редко, т.к. сами команды тоже очень редко встречаются.
- `init`, `destroy` — для управления ресурсами в момент создания сервлета и в момент его уничтожения.

```java
public class NewServlet extends HttpServlet {
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Параметр
        String parameter = request.getParameter("parameter");

        // Старт HTTP сессии
        HttpSession session = request.getSession(true);
        session.setAttribute("parameter", parameter);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<" +
                    "ad>");
            out.println("<title>Заголовок</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Пример сервлета"+parameter+"</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    } 

    @Override
    public String getServletInfo() {
        return "Пример сервлета";
    }

}
```


### Сервлет vs CGI

- Сервлеты запускаются в одном процессе (HTTP-сервер с дополнительными функциями, который называется Serlet Container), и они существуют до тех пор, пока этот процесс существует.
- CGI каждый раз создает новый экземпляр процесса для обслуживания запроса. Это убийца перфоманса.
- Поскольку для каждого запроса существует новый процесс, это означает, что CGI не может агрегировать данные из нескольких запросов в памяти.

### Сервлет vs FastCGI

- При использовании сервлетов веб-сервер может напрямую вызвать приложение.

## Контейнеры сервлетов. Жизненный цикл сервлета.

### Контейнеры сервлетов

__Контейнер сервлетов__ — программа, представляющая собой сервер, который занимается системной поддержкой сервлетов и обеспечивает их жизненный цикл в соответствии с правилами, определёнными в спецификациях.

### 2. Жизненный цикл сервлета 

Жизненный цикл сервлета состоит из следующих шагов:

- В случае отсутствия сервлета в контейнере.
1. Класс сервлета загружается контейнером.
2. Контейнер создает экземпляр класса сервлета.
3. Контейнер вызывает метод init(). 
- Обслуживание клиентского запроса. Каждый запрос обрабатывается в своем отдельном потоке. Контейнер вызывает метод service() для каждого запроса. Этот метод определяет тип пришедшего запроса и распределяет его в соответствующий этому типу метод для обработки запроса. Разработчик сервлета должен предоставить реализацию для этих методов. Если поступил запрос, метод для которого не реализован, вызывается метод родительского класса и обычно завершается возвращением ошибки инициатору запроса.
- В случае если контейнеру необходимо удалить сервлет, он вызывает метод destroy(), который снимает сервлет из эксплуатации. Подобно методу init(), этот метод тоже вызывается единожды за весь цикл сервлета.

## 3. Диспетчеризация запросов в сервлетах. Фильтры сервлетов.

### Диспетчеризация запросов в сервлетах

- Сервлеты могут делегировать обработку запросов
другим ресурсам (сервлетам, JSP и HTML-страницам).
- Диспетчеризация осуществляется с помощью
реализаций интерфейса
`javax.servlet.RequestDispatcher`.
- Два способа получения `RequestDispatcher` — через
`ServletRequest` (абсолютный или относительный URL)
и `ServletContext` (только абсолютный URL).
- Два способа делегирования обработки запроса —
`forward` и `include`.

### Фильтр сервлетов
Сервлетный фильтр занимается предварительной обработкой запроса, прежде чем тот попадает в сервлет, и/или последующей обработкой ответа, исходящего из сервлета.

Сервлетные фильтры могут:

- перехватывать инициацию сервлета прежде, чем сервлет будет инициирован;
- определить содержание запроса прежде, чем сервлет будет инициирован;
- модифицировать заголовки и данные запроса, в которые упаковывается поступающий запрос;
- модифицировать заголовки и данные ответа, в которые упаковывается получаемый ответ;
- перехватывать инициацию сервлета после обращения к сервлету.

Основой для формирования фильтров служит интерфейс `javax.servlet.Filter`, который реализует три метода:

`void init (FilterConfig config) throws ServletException;`

`void destroy ();`

`void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;`

## 4. HTTP-сессии - назначение, взаимодействие сервлетов с сессией, способы передачи идентификатора сессии.

Сеанс (сессия) – соединение между клиентом и сервером, устанавливаемое на определенное время, за которое клиент может отправить на сервер сколько угодно запросов. Сеанс устанавливается непосредственно между клиентом и Web-сервером. Каждый клиент устанавливает с сервером свой собственный сеанс.

Чтобы открыть новый сеанс, используется метод `getSession()` интерфейса `HttpServletRequest`. Метод извлекает из переданного в сервлет запроса объект сессии класса `HttpSession`, соответствующий данному пользователю.

Чтобы сохранить значения переменной в текущем сеансе, используется метод `setAttribute()`, прочесть – `getAttribute()`, удалить – `removeAttribute()`. Список имен всех переменных, сохраненных в текущем сеансе, можно получить, используя метод `Enumeration getAttributeNames()`.

### Способы передачи идентификатора сессии

Есть 3 способа отслеживания сессии:

1. cookies
2. переопределяемый URL (используется response.encodeURL() для каждой ссылки, который вставляет идентификатор сессии в каждый URL.)
3. cкрытые поля форм

Создание cookies

```java
Cookie c = new Cookie (name, value);
public interface HttpSession {
    public void invalidate();
    ...
}
```

## 5. Контекст сервлета - назначение, способы взаимодействия сервлетов с контекстом.

Контекст сервлета - API, с помощью которого сервлет можетвзаимодействовать со своим контейнером.

 Сервлет может получать информацию о своем окружении в различное время. Во время запуска сервлета доступна информация об инициализации; информация о сервере доступна в любое время, кроме этого, любой запрос может содержать дополнительную специфическую информацию.
 
Информация о контексте сервера доступна через метод `getServletContext()` объекта `ServletConfig` (этот объект передается сервлету во время инициализации). Метод init() должен сохранять private  ссылку в переменной. 

`getAttribute ()`	Гибкий способ получения информации о сервере через пары атрибутов имя/значение. Зависит от сервера.

`GetMimeType ()`	Возвращает тип MIME данного файла.

`getRealPath ()`	Этот метод преобразует относительный или виртуальный путь в новый путь относительно месторасположения корня HTML-документов сервера.

`getServerInfo ()`	Возвращает имя и версию сетевой службы, в которой исполняется сервлет.

`getServlet ()`	Возвращает объект Servlet указанного имени. Полезен при доступе к службам других сервлетов.

`getServletNames ()`	Возвращает список имен сервлетов, доступных в текущем пространстве имен.

`log ()`	Записывает информацию в файл регистрации сервлета. Имя файла регистрации и его формат зависят от сервера.

## 6. JavaServer Pages. Особенности, преимущества и недостатки по сравнению с сервлетами, область применения.

Jsp - технология, позволяющая веб-разработчикам создавать содержимое, которое имеет как статические, так и динамические компоненты, 
(а так же позволяющая отделить бизнес-логику от уровня
представления)

 При загрузке в веб-контейнер страницы JSP
транслируются компилятором (jasper) в сервлеты.

JavaServer Pages (JSP) позволяют отделить динамическую часть страниц от статического HTML. Динамическая часть заключается в специальные теги "<% %>":

```html
Спасибо за покупку 
<I><%= request.getParameter("title") %></I>
```

#### Преимущества:

● Высокая производительность — транслируются в сервлеты.
● Не зависят от используемой платформы — код пишется на Java.
● Позволяют использовать Java API.
● Простые для понимания — структура похожа на обычный HTML.

#### Недостатки:

● Трудно отлаживать, если приложение целиком основано на JSP.
● Возможны конфликты при параллельной обработке нескольких запросов.


### Jsp vs сервлет

jsp позволяет писать текст шаблона на клиентских языках (например, HTML, CSS, JavaScript и т.д.), которые поддерживаются кусками кода Java

JSP также поддерживает язык выражений, который может использоваться для доступа к базовым данным (через атрибуты, доступные на странице, в запросе, в сеансах и в приложениях)

Сервлет работает на сервере, перехватывает запросы, сделанные клиентом, и генерирует/отправляет ответ

## 7. Жизненный цикл JSP.

Конвертацией JSP страниц в HTML код занимается контейнер.

Жизненный цикл:

1. Translation – JSP контейнер проверяет код JSP страницы, парсит ее для создания кода сервлета. 
2. Compilation – JSP контейнер компилирует исходный код jsp класса и создает класс на этой фазе.
3. Class Loading – контейнер загружает классы в память на этой фазе.
4. Instantiation – внедрение конструкторов без параметров созданных классов для инициализации в памяти классов.
5. Initialization – в контейнере вызывается init метод объекта JSP класса и инициализируется конфигурация сервлета с init параметрами, которые указаны в дескрипторе развертывания (web.xml).
6. Request Processing – длительный жизненный цикл  обработки запросов клиента JSP страницей. Обработка является многопоточной и аналогична сервлетам — для каждого запроса создается новая нить, создаются объекты ServletRequest и ServletResponse и происходит внедрение сервис методов JSP.
7. Destroy – последняя фаза жизненного цикла JSP на которой JSP класс удаляется из памяти. Обычно это происходит при выключении сервера или андеплое приложения.

Методы: `jspInit() `, `_jspService()`, `jspDestroy()`

## 8. Структура JSP-страницы. Комментарии, директивы, объявления, скриптлеты и выражения.

### Комментарии

В JSP-страницах комментарии можно разделить на две группы:

- комментарии исходного кода JSP
- комментарии HTML-разметки.

Комментарии исходного кода JSP отмечаются специальной последовательностью символов: `<%--` в начале и `--%>` в конце комментария. Данный вид комментариев удаляется на этапе компиляции JSP-страницы. 

Комментарии HTML-разметки оформляются в соответствии с правилами языка HTML. Данный вид комментариев рассматривается JSP-компилятором как статический текст и помещается в выходной HTML-документ. JSP-выражения внутри HTML-комментариев исполняются.

```jsp
<%--
	Отобразит каталог изделий
	и актуальную корзину покупателя.
--%>
```

```jsp
<!-- Дата создания страницы: <%= new java.util.Date() %> -->
```

### Директивы

JSP страница может послать сообщение соответствующему контейнеру с указаниями действий, которые необходимо провести. Эти сообщения называются директивами. Все директивы начинаются с `<%@`, затем следует название директивы и атрибуты со значениями, и заканчиваются `%>`. Директивы в JSP странице приводят к тому, что контейнер пошлёт заявку на исполнение определённой службы, которая в генерированном документе не объявляется.

```jsp
<%@ директива атрибут1="значение1" 
              атрибут2="значение2"
              ...
              атрибутN="значениеN" %>
```

### Объявления

Объявления JSP позволят вам задавать переменные, методы, внутренние классы и так далее. Объявления используются для определения используемых в программе конструкций Java. 

```jsp
<%! private int accessCount = 0; %>
Количество обращений к странице с момента загрузки сервера: <%= ++accessCount %>
```

### Cкриптлеты

Скриплеты JSP дают возможность вставить любой код в метод сервлета, который будет создан при обработке страницы, позволяя использовать большинство конструкций Java. 

```jsp
<% 
String queryData = request.getQueryString();
out.println("Дополнительные данные запроса: " + queryData); 
%>
```

### Выражения

Выражения JSP применяются для того, чтобы вставить значения Java непосредственно в вывод. Выражения Java вычисляются, конвертируются в строку и вставляются в страницу. 

```jsp
Текущее время: <%= new java.util.Date() %>
Имя вашего хоста: <%= request.getRemoteHost() %>
```

## 9. Правила записи Java-кода внутри JSP. Стандартные переменные, доступные в скриптлетах и выражениях.

Синтаксис записи Java-кода внутри JSP представляется следующим образом:
```jsp
<% 
  Java-код 
	   %>
```
Стандартные переменные, доступные в скриптлетах и выражениях :
`request`,`response`,`session`,`HttpSession`,`HttpServletRequest`,`HttpServletResponse`,`PrintWriter`

## 10. Bean-компоненты и их использование в JSP.

JavaBeans — классы в языке Java, написанные по определённым правилам. Они используются для объединения нескольких объектов в один для удобной передачи данных. 

Чтобы класс мог работать как bean, он должен соответствовать определённым соглашениям об именах методов, конструкторе и поведении. Эти соглашения дают возможность создания инструментов, которые могут использовать, замещать и соединять JavaBeans.

Правила описания:

- Класс должен иметь конструктор без параметров, с модификатором доступа public. Такой конструктор позволяет инструментам создать объект без дополнительных сложностей с параметрами.
- Свойства класса должны быть доступны через get, set и другие методы (так называемые методы доступа), которые должны подчиняться стандартному соглашению об именах. Это легко позволяет инструментам автоматически определять и обновлять содержание bean’ов. 
- Класс должен быть сериализуем. Это даёт возможность надёжно сохранять, хранить и восстанавливать состояние bean независимым от платформы и виртуальной машины способом.
- Класс должен иметь переопределенные методы equals(), hashCode() и toString().

### Bean и JSP

Устанавить связь между JSP и соответствующим ей бин-компонентом

```jsp
<jsp:useBean id="_loginJSPBean" class="lbm.examples.LoginJSPBean" scope="session"/> 
```

Передать значения всех полей формы

## 11. Стандартные теги JSP. Использование Expression Language (EL) в JSP.

Для подключения библиотеки тегов JSTL используются следующие выражения:
```jsp
// Основные теги создания циклов, определения условий, вывода информации на страницу и т.д.
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
// Теги для работы с XML-документами
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
// Теги для работы с базами данных 
<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/sql" %>
// Теги для форматирования и интернационализации информации (i10n и i18n)
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
```
К стандартным тегам JSP можно отнести:
`<c:out>`,`<c:set>`,`<c:remove>`,`<c:if>`,`<c:choose>`,`<c:forEach>`,`<c:import>`,`<c:catch>`

Expression Language или сокращенно EL предоставляет компактный синтаксис для обращения к массивам, коллекциям, объектам и их свойствам внутри страницы jsp. Он довольн прост. Вставку окрывает знак $, затем в фигурные скобки {} заключается выводимое значение:
```jsp
${attribute} 
${object.property}
```

## 12. Параметры конфигурации JSP в дескрипторе развёртывания веб-приложения.

Java веб-приложения используют файл дескриптора развертывания для определения какие URL будут передаваться определенному сервлету, какие URL требуют аутентификации и др. Этот дескриптор развертывания называется web.xml и находится в WAR приложения в  WEB-INF/ директории. Web.xml – часть стандарта сервлета для веб-приложений.

## 13. Шаблоны проектирования и архитектурные шаблоны. Использование в веб-приложениях.

Основные архитектурные шаблоны:

- Многоуровневая архитектура
- Каналы и фильтры
- Клиент — сервер
- Модель — представление — контроллер
- Управляемая событиями архитектура
- Архитектура на основе микросервисов

Основные шаблоны проектирования:

- простая фабрика (Simple Factory);
- фабричный метод (Factory Method);
- абстрактная фабрика (Abstract Factory);
- строитель (Builder);
- прототип (Prototype);
- одиночка (Singleton).

## 14. Архитектура веб-приложений. Шаблон MVC. Архитектурные модели Model 1 и Model 2 и их реализация на платформе Java EE.

__Model-View-Controller__ — схема разделения данных приложения, пользовательского интерфейса и управляющей логики на три отдельных компонента: модель, представление и контроллер — таким образом, что модификация каждого компонента может осуществляться независимо.

- Модель (Model) предоставляет данные и методы работы с ними: запросы в базу данных, проверка на корректность (не зависит от представления (не знает как данные визуализировать) и контроллера (не имеет точек взаимодействия с пользователем).
- Представление (View) отвечает за получение необходимых данных из модели и отправляет их пользователю (не обрабатывает введённые данные пользователя).
- Контроллер (Controller) обеспечивает «связи» между пользователем и системой. Контролирует и направляет данные от пользователя к системе и наоборот.
