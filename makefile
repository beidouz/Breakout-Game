NAME = "Main"
SRC = "src"
OUT = "out"

all:
	@echo "Compiling..."
	[ -d $(OUT) ] || mkdir $(OUT)
	javac -cp $(SRC) -d $(OUT) $(SRC)/*.java

run: all
	@echo "Running..."
	java -cp $(OUT) $(NAME)

clean:
	rm -rf $(OUT)/*.class
