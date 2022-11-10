// live template expression
// groovyScript(concat(substringBefore(filePath(), fileName()), "inline.groovy"), clipboard());

def cb = _1
def ls = System.lineSeparator()
return cb.split(ls).collect { l ->
  def w = l.split("\"", -1)
  if (w.size() == 3) { // single quoted string per a line
    def s = w[1].split(":")
    switch(s.size()) {
      case 2: // plugins
        "${w[0]} { id = \"${s[0]}\", version = \"${s[1]}\" }${w[2]}"
        break
      case 3: // libraries
        "${w[0]} { module = \"${s[0]}:${s[1]}\", version = \"${s[2]}\" }${w[2]}"
        break
      default: // NG?
        "${l} ###"
    }
  } else {
    l
  }
}.join(ls)
