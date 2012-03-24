CHECKERS_PATH = "tools/checkers"

# Check the document for common errors
task :check => ['check:all']

namespace :check do
  desc "Check the document for common errors"
  task :all => [:duplicates, :passive, :weasel, :syntax, :spell]

  desc "Check the document for syntax errors"
  task :syntax do
    puts "Checking the file for syntax errors... [#{TEX_NAME}]"
    print `lacheck #{TEX_NAME}`
  end

  desc "Check the document for accidental duplicate words"
  task :duplicates do
    puts "Checking the file for duplicate words... [#{TEX_NAME}]"
    print perl "#{CHECKERS_PATH}/illusion.pl", TEX_NAME
  end

  desc "Check the document for passive speech"
  task :passive do
    puts "Checking the file for passive speech... [#{TEX_NAME}]"
    print bash "#{CHECKERS_PATH}/passive.sh", TEX_NAME
  end

  desc "Check the document for words which are not useful"
  task :weasel do
    puts "Checking the file for words that aren't needed... [#{TEX_NAME}]"
    print bash "#{CHECKERS_PATH}/weasel.sh", TEX_NAME
  end

  desc "Spell check the document"
  task :spell do
    puts "Checking the file for spelling mistakes... [#{TEX_NAME}]"
    aspell "check", "--mode=tex", TEX_NAME
  end
end

desc "Count the number of words in the document"
task :word_count do
  puts "Counting the words in the document... [#{TEX_NAME}]"
  perl "tools/texcount.pl", TEX_NAME
end
task :count => [:word_count]