json="json_xs"
#json="python -mjson.tool"

pgf="http://localhost:41296/grammars/Go.pgf"

curl "$pgf?command=grammar" | $json

curl "$pgf?command=parse&cat=Number&from=GoEng&input=three" | $json

curl "$pgf?command=linearize&to=GoEng&tree=n3" | $json

curl "$pgf?command=translate&cat=Number&from=GoEng&to=GoApp&input=three" | $json

curl "$pgf?command=random&cat=Number&input=t" | $json

curl "$pgf?command=complete&cat=Number&input=t" | $json

curl "$pgf?command=abstrtree&tree=n3" > diagram_abstrtree.png

curl "$pgf?command=parsetree&tree=n3&from=GoEng&format=gv" > diagram_parsetree.dot

curl "$pgf?command=alignment&tree=n3&format=svg" > diagram_alignment.svg