package com.simplemobiletools.notes.pro.new_architecture.shared.extensions

import java.io.BufferedWriter


fun BufferedWriter.writeLn(line: String) {
    write(line)
    newLine()
}
