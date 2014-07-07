require 'open-uri'
require 'json'

resp = open('https://api.clever.com/v1.1/sections', http_basic_authentication: ['DEMO_KEY','']).read
sections = JSON.parse(resp)
num_sections = 0
total_students = 0
max_students = 0
min_students = 0
max_section = nil
min_section = nil
sections["data"].each do |section|
	num_sections += 1
	total_students += section["data"]["students"].size
end

puts "The average number of students per section is: #{total_students.to_f/num_sections.to_f}"