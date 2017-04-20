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
        if (line.startswith("/") != True and len(line) > 3):
            input_string += line + " "
    return input_string


def read_raw_as_lines(in_file_name):
    in_file = open(in_file_name, "r")
    input_lines = []
    for line in in_file:
        if (line.startswith("/") != True and len(line) > 3):
            input_lines.append(line)
    return input_lines


def read_annotations(in_file_name):
    in_file = open(in_file_name, "r")
    input_lines = []
    for line in in_file:
        if (line.startswith("/") == True and len(line) != 0):
            input_lines.append(line)
    return input_lines

def array_to_string(array):
    output = ""
    for string in array:
        output += string;

    return output;