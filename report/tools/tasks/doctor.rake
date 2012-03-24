def check_for(program)
  installed = command?(program)
  
  unless installed
    puts ">>> #{program} was not found in your PATH."
    
    locate_output = `locate '*/#{program}'`
    bin_re = Regexp.new("\S*\/bin\/\S*\/#{program}")
    
    puts "    However, the following executables were found that may help:"
    puts "    \t#{locate_output.match(bin_re).to_s}"
    puts
    puts ">>> If no executables were found then you probably haven't"
    puts "    installed something. To Google!"
  end
  
  installed
end

def os?
  case RUBY_PLATFORM
    when /darwin/i
      :osx
    when /linux/i
      :linux
    when /win32/i
      :win
  end
end

def full_name
  case os?
  when :osx then `osascript -e "long user name of (system info)"`.chomp
  when :linux then `getent passwd $USER | cut -d ":" -f 5 | tr -d ","`.chomp
  when :win then "Informatician"
  end
end

def first_name
  full_name.split.first
end

desc "Check your system for missing dependencies"
task :doctor do
  puts ">>> Right then #{first_name}, let's see if everything's shiny."
  
  ok = true
  
  ok && check_for(:xelatex)
  ok && check_for(:bibtex)
  ok && check_for(:aspell)
  ok && check_for(:lacheck)
  ok && check_for(:perl)
  
  puts ">>> Looks like you're ready to go!" if ok
end