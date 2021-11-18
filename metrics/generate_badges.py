def main():

    import os

    code_coverage = get_code_coverage()
    verify_unit_tests(code_coverage)
    code_coverage, code_coverage_color = number_to_badge(code_coverage)
    os.system("wget --output-document=code_coverage.svg https://img.shields.io/badge/code%20coverage-{}-{}".format
              (code_coverage, code_coverage_color))


def get_code_coverage():

    import xml.etree.ElementTree as ET

    tree = ET.parse("../android/build/my-reports/result.xml")
    root = tree.getroot()
    classes = root[0].findall('sourcefile')
    lines_covered, lines_missed = 0, 0
    classes_excluded = ['BuildConfig.java']
    for element in classes:
        if element.attrib['name'] in classes_excluded:
            continue
        counters = element.findall('counter')
        lines_covered += int(counters[2].attrib['covered'])
        lines_missed += int(counters[2].attrib['missed'])

    return lines_covered / (lines_covered + lines_missed)


def number_to_badge(number):

    color = get_color(number)
    if number == 1:
        return "100%25", color
    number *= 100
    return str(round(number, 1)) + "%25", color


def get_color(number):

    if number < 0.6:
        return "red"
    if number < 0.8:
        return "yellow"
    if number < 0.92:
        return "green"
    return "brightgreen"


def verify_unit_tests(number):

    if number < 0.92:
        print("Code coverage < 92%")
        exit(-1)


if __name__ == "__main__":

    main()
