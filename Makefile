# 변수 선언
REPO:=/home/user/Snort_WAS_board
SERVLET_JAR:=/opt/tomcat/lib/servlet-api.jar

BOARD_CLASS:=$(REPO)/board/WEB-INF/classes
MENU_SRC:=$(REPO)/board/src/GetMenuServlet.java
TITLE_SRC:=$(REPO)/board/src/GetTitleServlet.java
GSON_JAR:=$(REPO)/board/WEB-INF/lib


# define command
.PHONY: pull make_folders down menu title restart set build clean


# git pull
pull:
	git pull

# 폴더 생성
make_folders:
	mkdir -p $(BOARD_CLASS)
	mkdir -p $(GSON_JAR)

# 필요 파일 download
down:
	curl -o $(GSON_JAR)/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# compile
menu:
	javac -classpath "$(SERVLET_JAR):$(GSON_JAR)/gson-2.10.1.jar" -d $(BOARD_CLASS) $(MENU_SRC)
title:
	javac -classpath "$(SERVLET_JAR):$(GSON_JAR)/gson-2.10.1.jar" -d $(BOARD_CLASS) $(TITLE_SRC)

# systemctl
restart:
	systemctl stop tomcat && systemctl start tomcat


# commands
set: make_folders down
build: menu title restart


# compile 파일 초기화
clean:
	rm -f $(BOARD_CLASS)/GetMenuServlet.class
	rm -f $(BOARD_CLASS)/GetTitleServlet.class
