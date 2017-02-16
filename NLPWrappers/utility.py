import re


#Reads from sentence-splitting format
comment_pattern = re.compile("//.*")
def read_splits(in_file_name):
    in_file = open(in_file_name, "r")
    input_string = ""
    for line in in_file:
        line = line.strip()
        current_line = line.split("\t")

        if  len(current_line) == 2:
            if(line.startswith("/") != True):
                # print(current_line[1])
                input_string += (current_line[1].strip() + " ")
    print(input_string)
    return input_string


def read_raw(in_file_name):
    in_file = open(in_file_name, "r")
    input_string = ""
    for line in in_file:
        input_string += line + " "
    return input_string