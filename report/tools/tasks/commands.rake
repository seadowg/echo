# Helper Functions
def command(command_name, *arguments)
  args = arguments.flatten.join(' ')
  `#{command_name} #{args}`
end

def command?(name)
  `which #{name}`
  $?.success?
end

def latex(*args)
  command TEX_EXEC, args
end

def perl(*args)
  command "perl", args
end

def aspell(*args)
  sh "aspell #{args.flatten.join " "}"
end

def bash(*args)
  command "bash", args
end
