# 변수 선언
REPO:=/home/user/Snort_WAS_board
SERVLET_JAR:=/opt/tomcat/lib/servlet-api.jar
GSON_BOARD_JAR:=$(REPO)/board/WEB-INF/lib
GSON_ARTICLE_JAR:=$(REPO)/article/WEB-INF/lib

BOARD_CLASS:=$(REPO)/board/WEB-INF/classes
MENU_SRC:=$(REPO)/board/src/GetMenuServlet.java
TITLE_SRC:=$(REPO)/board/src/GetTitleServlet.java

ARTICLE_CLASS:=$(REPO)/article/WEB-INF/classes
ARTICLE_SRC:=$(REPO)/article/src/GetArticleServlet.java


# define command
.PHONY: pull make_folders delete down menu title article restart set build clean


# git pull
pull:
	git pull

# 폴더 생성
make_folders:
	mkdir -p $(BOARD_CLASS)
	mkdir -p $(GSON_BOARD_JAR)
	mkdir -p $(ARTICLE_CLASS)
	mkdir -p $(GSON_ARTICLE_JAR)

# 필요 파일 download
delete:
	rm -f $(GSON_BOARD_JAR)/gson-2.10.1.jar
	rm -f $(GSON_ARTICLE_JAR)/gson-2.10.1.jar
down:
	curl -o $(GSON_BOARD_JAR)/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
	curl -o $(GSON_ARTICLE_JAR)/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# compile
menu:
	javac -classpath "$(SERVLET_JAR):$(GSON_BOARD_JAR)/gson-2.10.1.jar" -d $(BOARD_CLASS) $(MENU_SRC)
title:
	javac -classpath "$(SERVLET_JAR):$(GSON_BOARD_JAR)/gson-2.10.1.jar" -d $(BOARD_CLASS) $(TITLE_SRC)
article:
	javac -classpath "$(SERVLET_JAR):$(GSON_ARTICLE_JAR)/gson-2.10.1.jar" -d $(ARTICLE_CLASS) $(ARTICLE_SRC)

# systemctl
restart:
	systemctl stop tomcat && systemctl start tomcat


# commands
set: make_folders delete down
build: menu title article restart


# compile 파일 초기화
clean:
	rm -f $(BOARD_CLASS)/GetMenuServlet.class
	rm -f $(BOARD_CLASS)/GetTitleServlet.class
	rm -f $(ARTICLE_CLASS)/GetArticleServlet.class
