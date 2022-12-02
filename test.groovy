selection = _1

def parsed = selection.split(":")

def group = parsed[0]
def artifact = parsed[1]

def result = """
group    = ${group}
artifact = ${artifact}
"""

return result