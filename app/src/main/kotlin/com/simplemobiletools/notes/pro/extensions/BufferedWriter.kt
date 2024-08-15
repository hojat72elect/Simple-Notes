package com.simplemobiletools.notes.pro.extensions

import java.io.BufferedWriter


fun BufferedWriter.writeLn(line: String) {
    write(line)
    newLine()
}
