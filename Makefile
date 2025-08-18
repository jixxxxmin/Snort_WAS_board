# 변수 선언
REPO:=/home/user/Snort_WAS_board
SERVLET_JAR:=/opt/tomcat/lib/servlet-api.jar

MENU_SRC:=$(REPO)/board/src/GetMenuServlet.java
MENU_CLASS:=$(REPO)/board/WEB-INF/classes
GSON_JAR:=/opt/lib/gson-2.10.1.jar


# define command
.PHONY: pull menu restart all clean


# git pull
pull:
	git pull

# compile
menu:
	javac -classpath "$(SERVLET_JAR):$(GSON_JAR)" -d $(MENU_CLASS) $(MENU_SRC)

# systemctl
restart:
	systemctl stop tomcat && systemctl start tomcat


# commands
all: pull menu restart


# compile 파일 초기화
clean:
	rm -f $(MENU_CLASS)/GetMenuServlet.class
