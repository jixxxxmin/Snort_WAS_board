# 변수 선언
REPO:=/home/user/Snort_WAS_board
SERVLET_JAR:=/opt/tomcat/lib/servlet-api.jar

MENU_SRC:=$(REPO)/board/src/GetMenuServlet.java
MENU_CLASS:=$(REPO)/board/WEB-INF/classes
GSON_JAR:=$(REPO)/board/WEB-INF/lib


# define command
.PHONY: pull make_folders down menu restart set build clean


# git pull
pull:
	git pull

# 폴더 생성
make_folders:
	mkdir -p $(MENU_CLASS)
	mkdir -p $(GSON_JAR)

# 필요 파일 download
down:
	curl -o $(GSON_JAR)/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# compile
menu:
	javac -classpath "$(SERVLET_JAR):$(GSON_JAR)/gson-2.10.1.jar" -d $(MENU_CLASS) $(MENU_SRC)

# systemctl
restart:
	systemctl stop tomcat && systemctl start tomcat


# commands
set: make_folders down
build: menu restart


# compile 파일 초기화
clean:
	rm -f $(MENU_CLASS)/GetMenuServlet.class
