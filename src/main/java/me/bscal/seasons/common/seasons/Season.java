package me.bscal.seasons.common.seasons;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public enum Season
{

    Spring,
    Summer,
    Autumn,
    Winter;

    public static final int MAX_SEASONS = 4;
    public static final int MAX_SEASON_ID = MAX_SEASONS - 1;

    public static class SeasonArgumentType implements ArgumentType<Season>
    {
        public Season parse(StringReader stringReader) throws CommandSyntaxException
        {
            String string = stringReader.readUnquotedString();
            try
            {
                return valueOf(string);
            } catch (Exception ex)
            {
                throw INVALID_ENUM_EXCEPTION.create(string);
            }
        }

        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
        {
            return CommandSource.suggestMatching(Arrays.stream(values()).map(Enum::toString).collect(Collectors.toList()), builder);
        }

        public Collection<String> getExamples()
        {
            return Arrays.stream(values()).map((object) -> toString()).limit(2L).collect(Collectors.toList());
        }

        private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType((object) ->
        {
            return Text.translatable("argument.enum.invalid", new Object[]{object});
        });
    }
}
